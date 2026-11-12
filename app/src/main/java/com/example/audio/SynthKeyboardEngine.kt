package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

private const val TAG = "SynthKeyboardEngine"
private const val SAMPLE_RATE = 44100

/**
 * Types of synthesizer instruments playable on the keyboard.
 */
enum class SynthInstrument(val displayName: String, val description: String) {
    SYNTH_LEAD("Lead Synth", "Punchy melodic hook synthesizer"),
    BASS_808("808 Tuned Bass", "Deep sub-harmonic tuned 808 bass"),
    RHODES_PIANO("E-Piano", "Warm bell-toned electric keys"),
    PLUCK("Synth Pluck", "Crisp percussive stabs")
}

/**
 * Musical note definition for the keyboard keys.
 */
data class KeyboardNote(
    val name: String,
    val noteOffset: Int, // Semitones from C (0..12)
    val isBlackKey: Boolean
) {
    companion object {
        val CHROMATIC_OCTAVE = listOf(
            KeyboardNote("C", 0, isBlackKey = false),
            KeyboardNote("C#", 1, isBlackKey = true),
            KeyboardNote("D", 2, isBlackKey = false),
            KeyboardNote("D#", 3, isBlackKey = true),
            KeyboardNote("E", 4, isBlackKey = false),
            KeyboardNote("F", 5, isBlackKey = false),
            KeyboardNote("F#", 6, isBlackKey = true),
            KeyboardNote("G", 7, isBlackKey = false),
            KeyboardNote("G#", 8, isBlackKey = true),
            KeyboardNote("A", 9, isBlackKey = false),
            KeyboardNote("A#", 10, isBlackKey = true),
            KeyboardNote("B", 11, isBlackKey = false),
            KeyboardNote("C+", 12, isBlackKey = false)
        )

        val WHITE_KEYS = CHROMATIC_OCTAVE.filter { !it.isBlackKey }
        val BLACK_KEYS = CHROMATIC_OCTAVE.filter { it.isBlackKey }
    }
}

/**
 * SynthKeyboardEngine generates real-time musical synthesizer notes and one-shot
 * drum hits using low-latency AudioTrack synthesis so users can play melodies and
 * live drum beats over their rhythm tracks.
 */
class SynthKeyboardEngine(private val scope: CoroutineScope) {

    private val _currentInstrument = MutableStateFlow(SynthInstrument.SYNTH_LEAD)
    val currentInstrument: StateFlow<SynthInstrument> = _currentInstrument.asStateFlow()

    private val _currentOctave = MutableStateFlow(4) // Default Octave 4 (Middle C)
    val currentOctave: StateFlow<Int> = _currentOctave.asStateFlow()

    private val _lastPlayedNote = MutableStateFlow<String?>(null)
    val lastPlayedNote: StateFlow<String?> = _lastPlayedNote.asStateFlow()

    private val _lastTriggeredDrum = MutableStateFlow<DrumSound?>(null)
    val lastTriggeredDrum: StateFlow<DrumSound?> = _lastTriggeredDrum.asStateFlow()

    // Pre-computed sample cache for snappy, zero-allocation response
    private val noteSampleCache = ConcurrentHashMap<String, ShortArray>()

    fun setInstrument(instrument: SynthInstrument) {
        _currentInstrument.value = instrument
    }

    fun setOctave(octave: Int) {
        _currentOctave.value = octave.coerceIn(2, 6)
    }

    fun octaveUp() {
        if (_currentOctave.value < 6) _currentOctave.value++
    }

    fun octaveDown() {
        if (_currentOctave.value > 2) _currentOctave.value--
    }

    /**
     * Plays a musical keyboard note in real-time.
     */
    fun playNote(note: KeyboardNote) {
        val octave = _currentOctave.value
        val instrument = _currentInstrument.value
        _lastPlayedNote.value = "${note.name}$octave"

        // Calculate frequency using 12-TET tuning: MIDI note = (octave + 1) * 12 + noteOffset
        val midiNote = (octave + 1) * 12 + note.noteOffset
        val frequency = 440.0 * Math.pow(2.0, (midiNote - 69) / 12.0)

        scope.launch(Dispatchers.Default) {
            try {
                val cacheKey = "${instrument.name}_${midiNote}"
                val pcmData = noteSampleCache.getOrPut(cacheKey) {
                    synthesizeNote(frequency, instrument, durationSec = 0.42)
                }
                playStaticPcm(pcmData)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to play note $note", e)
            }
        }
    }

    /**
     * Plays a drum voice one-shot hit in real-time.
     */
    fun playDrum(sound: DrumSound) {
        _lastTriggeredDrum.value = sound
        scope.launch(Dispatchers.Default) {
            try {
                val pcmData = noteSampleCache.getOrPut("DRUM_${sound.name}") {
                    synthesizeDrumSound(sound)
                }
                playStaticPcm(pcmData)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to play drum sound $sound", e)
            }
        }
    }

    /**
     * Synthesizes audio samples for a specific musical pitch and instrument.
     */
    private fun synthesizeNote(freq: Double, instrument: SynthInstrument, durationSec: Double): ShortArray {
        val sampleCount = (durationSec * SAMPLE_RATE).toInt()
        val buffer = ShortArray(sampleCount)

        for (i in 0 until sampleCount) {
            val t = i.toDouble() / SAMPLE_RATE
            val progress = (t / durationSec).coerceIn(0.0, 1.0)

            val rawWave = when (instrument) {
                SynthInstrument.SYNTH_LEAD -> {
                    // Sawtooth + Sine blend with warm low-pass decay
                    val sine = sin(2.0 * PI * freq * t)
                    val saw = (2.0 * ((freq * t) % 1.0) - 1.0) * 0.4
                    (sine * 0.7 + saw) * exp(-progress * 3.5)
                }
                SynthInstrument.BASS_808 -> {
                    // Deep tuned sub sine with punch transient
                    val pitchEnvelope = freq * (1.0 + 0.5 * exp(-progress * 25.0))
                    val sine = sin(2.0 * PI * pitchEnvelope * t)
                    // Soft overdrive saturation
                    val saturated = Math.tanh(sine * 1.4)
                    saturated * exp(-progress * 2.2)
                }
                SynthInstrument.RHODES_PIANO -> {
                    // Fundamental + bell overtone at 3x frequency
                    val fundamental = sin(2.0 * PI * freq * t) * exp(-progress * 4.0)
                    val bell = sin(2.0 * PI * (freq * 3.0) * t) * exp(-progress * 12.0) * 0.35
                    (fundamental + bell) * 0.85
                }
                SynthInstrument.PLUCK -> {
                    // Fast snappy pluck with sharp exponential envelope
                    val sine = sin(2.0 * PI * freq * t)
                    val harmonics = sin(2.0 * PI * (freq * 2.0) * t) * 0.3
                    (sine + harmonics) * exp(-progress * 9.0)
                }
            }

            // Global smooth envelope: 4ms attack, smooth release
            val attack = if (t < 0.004) t / 0.004 else 1.0
            val release = if (progress > 0.85) (1.0 - progress) / 0.15 else 1.0
            val sample = (rawWave * attack * release).coerceIn(-1.0, 1.0)
            buffer[i] = (sample * Short.MAX_VALUE * 0.90).toInt().toShort()
        }
        return buffer
    }

    /**
     * Synthesizes standalone drum voice buffers for instant one-shot pad triggers.
     */
    private fun synthesizeDrumSound(sound: DrumSound): ShortArray {
        return when (sound) {
            DrumSound.KICK -> {
                val samples = (0.28 * SAMPLE_RATE).toInt()
                val data = ShortArray(samples)
                for (i in 0 until samples) {
                    val t = i.toDouble() / SAMPLE_RATE
                    val p = t / 0.28
                    val freq = 145.0 * exp(-p * 9.0) + 42.0
                    val env = exp(-p * 6.5)
                    data[i] = (sin(2.0 * PI * freq * t) * env * Short.MAX_VALUE * 0.95).toInt().toShort()
                }
                data
            }
            DrumSound.SUB_808 -> {
                val samples = (0.45 * SAMPLE_RATE).toInt()
                val data = ShortArray(samples)
                for (i in 0 until samples) {
                    val t = i.toDouble() / SAMPLE_RATE
                    val p = t / 0.45
                    val freq = 120.0 * exp(-p * 6.0) + 38.0
                    val env = exp(-p * 3.8)
                    data[i] = (sin(2.0 * PI * freq * t) * env * Short.MAX_VALUE * 0.98).toInt().toShort()
                }
                data
            }
            DrumSound.SNARE -> {
                val samples = (0.18 * SAMPLE_RATE).toInt()
                val data = ShortArray(samples)
                val rand = java.util.Random(42)
                for (i in 0 until samples) {
                    val t = i.toDouble() / SAMPLE_RATE
                    val p = t / 0.18
                    val body = sin(2.0 * PI * 185.0 * t) * exp(-p * 18.0) * 0.45
                    val noise = (rand.nextDouble() * 2.0 - 1.0) * exp(-p * 10.0) * 0.55
                    data[i] = ((body + noise).coerceIn(-1.0, 1.0) * Short.MAX_VALUE * 0.9).toInt().toShort()
                }
                data
            }
            DrumSound.CLAP -> {
                val samples = (0.16 * SAMPLE_RATE).toInt()
                val data = ShortArray(samples)
                val rand = java.util.Random(1337)
                for (i in 0 until samples) {
                    val t = i.toDouble() / SAMPLE_RATE
                    val b1 = if (t < 0.012) exp(-t / 0.003) else 0.0
                    val b2 = if (t in 0.012..0.024) exp(-(t - 0.012) / 0.003) else 0.0
                    val b3 = if (t >= 0.024) exp(-(t - 0.024) / 0.04) else 0.0
                    val env = (b1 * 0.7 + b2 * 0.8 + b3 * 1.0).coerceIn(0.0, 1.0)
                    val noise = (rand.nextDouble() * 2.0 - 1.0) * env
                    data[i] = (noise * Short.MAX_VALUE * 0.85).toInt().toShort()
                }
                data
            }
            DrumSound.HI_HAT_CLOSED -> {
                val samples = (0.05 * SAMPLE_RATE).toInt()
                val data = ShortArray(samples)
                val rand = java.util.Random(999)
                var last = 0.0
                for (i in 0 until samples) {
                    val p = i.toDouble() / samples
                    val raw = rand.nextDouble() * 2.0 - 1.0
                    val hp = raw - last
                    last = raw
                    data[i] = (hp * exp(-p * 22.0) * 0.6 * Short.MAX_VALUE * 0.7).toInt().toShort()
                }
                data
            }
            DrumSound.HI_HAT_OPEN -> {
                val samples = (0.18 * SAMPLE_RATE).toInt()
                val data = ShortArray(samples)
                val rand = java.util.Random(999)
                var last = 0.0
                for (i in 0 until samples) {
                    val p = i.toDouble() / samples
                    val raw = rand.nextDouble() * 2.0 - 1.0
                    val hp = raw - last
                    last = raw
                    data[i] = (hp * exp(-p * 8.0) * 0.8 * Short.MAX_VALUE * 0.7).toInt().toShort()
                }
                data
            }
            DrumSound.TOM -> {
                val samples = (0.25 * SAMPLE_RATE).toInt()
                val data = ShortArray(samples)
                for (i in 0 until samples) {
                    val t = i.toDouble() / SAMPLE_RATE
                    val p = t / 0.25
                    val freq = 110.0 * exp(-p * 7.0) + 65.0
                    data[i] = (sin(2.0 * PI * freq * t) * exp(-p * 8.0) * Short.MAX_VALUE * 0.9).toInt().toShort()
                }
                data
            }
            DrumSound.RIMSHOT -> {
                val samples = (0.06 * SAMPLE_RATE).toInt()
                val data = ShortArray(samples)
                val rand = java.util.Random(777)
                for (i in 0 until samples) {
                    val t = i.toDouble() / SAMPLE_RATE
                    val p = t / 0.06
                    val click = sin(2.0 * PI * 1800.0 * t) * exp(-p * 45.0) * 0.6
                    val noise = (rand.nextDouble() * 2.0 - 1.0) * exp(-p * 30.0) * 0.4
                    data[i] = ((click + noise).coerceIn(-1.0, 1.0) * Short.MAX_VALUE * 0.85).toInt().toShort()
                }
                data
            }
        }
    }

    /**
     * Plays a pre-computed PCM buffer via a low-latency AudioTrack.
     */
    private fun playStaticPcm(pcm: ShortArray) {
        try {
            val byteSize = pcm.size * 2
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(byteSize)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(pcm, 0, pcm.size)
            track.play()

            // Automatically release track after duration
            scope.launch(Dispatchers.Default) {
                val durationMs = ((pcm.size.toDouble() / SAMPLE_RATE) * 1000).toLong() + 50
                kotlinx.coroutines.delay(durationMs)
                try {
                    track.stop()
                    track.release()
                } catch (ignored: Exception) {}
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error playing static PCM", e)
        }
    }

    fun release() {
        noteSampleCache.clear()
    }
}
