package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Random
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

private const val TAG = "DrumBeatEngine"
private const val SAMPLE_RATE = 44100
private const val DEFAULT_STEPS_PER_BAR = 16

/**
 * Basic percussive instruments supported by the procedural DSP synthesizer.
 */
enum class DrumSound(val displayName: String) {
    KICK("Kick Drum"),
    SNARE("Snare Drum"),
    HI_HAT_CLOSED("Closed Hi-Hat"),
    HI_HAT_OPEN("Open Hi-Hat"),
    CLAP("Hand Clap"),
    SUB_808("808 Sub Bass"),
    TOM("Low Tom"),
    RIMSHOT("Rimshot")
}

/**
 * Represents a 16-step rhythmic pattern across all percussive instruments.
 */
data class DrumPattern(
    val name: String,
    val grid: Map<DrumSound, BooleanArray>
) {
    fun isStepActive(sound: DrumSound, step: Int): Boolean {
        val steps = grid[sound] ?: return false
        return if (step in steps.indices) steps[step] else false
    }

    fun copyWithToggledStep(sound: DrumSound, step: Int): DrumPattern {
        val newGrid = grid.mapValues { (s, steps) ->
            val copy = steps.copyOf()
            if (s == sound && step in copy.indices) {
                copy[step] = !copy[step]
            }
            copy
        }
        return copy(grid = newGrid)
    }

    companion object {
        fun createEmpty(name: String = "Custom"): DrumPattern {
            val emptyGrid = DrumSound.entries.associateWith { BooleanArray(DEFAULT_STEPS_PER_BAR) { false } }
            return DrumPattern(name, emptyGrid)
        }

        fun fromSteps(name: String, stepMap: Map<DrumSound, List<Int>>): DrumPattern {
            val grid = DrumSound.entries.associateWith { sound ->
                val activeIndices = stepMap[sound] ?: emptyList()
                BooleanArray(DEFAULT_STEPS_PER_BAR) { it in activeIndices }
            }
            return DrumPattern(name, grid)
        }

        val HIP_HOP = fromSteps(
            name = "Hip-Hop / Boom Bap",
            stepMap = mapOf(
                DrumSound.KICK to listOf(0, 7, 10),
                DrumSound.SNARE to listOf(4, 12),
                DrumSound.HI_HAT_CLOSED to listOf(0, 2, 4, 6, 8, 10, 12, 14)
            )
        )

        val TRAP = fromSteps(
            name = "Trap",
            stepMap = mapOf(
                DrumSound.SUB_808 to listOf(0, 11),
                DrumSound.CLAP to listOf(8),
                DrumSound.HI_HAT_CLOSED to listOf(0, 2, 4, 6, 8, 10, 12, 14, 15)
            )
        )

        val POP = fromSteps(
            name = "Pop / Dance",
            stepMap = mapOf(
                DrumSound.KICK to listOf(0, 4, 8, 12),
                DrumSound.CLAP to listOf(4, 12),
                DrumSound.HI_HAT_OPEN to listOf(2, 6, 10, 14),
                DrumSound.HI_HAT_CLOSED to listOf(0, 4, 8, 12)
            )
        )

        val ROCK = fromSteps(
            name = "Rock / Alt",
            stepMap = mapOf(
                DrumSound.KICK to listOf(0, 8, 10),
                DrumSound.SNARE to listOf(4, 12),
                DrumSound.HI_HAT_CLOSED to listOf(0, 2, 4, 6, 8, 10, 12, 14)
            )
        )

        val COUNTRY = fromSteps(
            name = "Country Shuffle",
            stepMap = mapOf(
                DrumSound.KICK to listOf(0, 8),
                DrumSound.SNARE to listOf(4, 12),
                DrumSound.HI_HAT_CLOSED to listOf(0, 2, 3, 6, 8, 10, 11, 14)
            )
        )

        val RNB = fromSteps(
            name = "R&B Slow Groove",
            stepMap = mapOf(
                DrumSound.SUB_808 to listOf(0, 6, 10),
                DrumSound.SNARE to listOf(4, 12),
                DrumSound.HI_HAT_CLOSED to listOf(0, 2, 4, 6, 8, 10, 12),
                DrumSound.HI_HAT_OPEN to listOf(14)
            )
        )

        val LO_FI = fromSteps(
            name = "Lo-Fi Dusty Chill",
            stepMap = mapOf(
                DrumSound.KICK to listOf(0, 7, 10),
                DrumSound.SNARE to listOf(4, 12),
                DrumSound.HI_HAT_CLOSED to listOf(0, 2, 4, 6, 8, 10, 12, 14),
                DrumSound.RIMSHOT to listOf(14)
            )
        )

        val METRONOME = fromSteps(
            name = "Metronome Click",
            stepMap = mapOf(
                DrumSound.RIMSHOT to listOf(0, 4, 8, 12)
            )
        )
    }
}

/**
 * DrumBeatEngine generates percussive sounds using DSP synthesis and sequences
 * them using Android's low-latency AudioTrack streaming API at specified BPMs.
 */
class DrumBeatEngine(private val scope: CoroutineScope) {

    private var audioTrack: AudioTrack? = null
    private var playbackJob: Job? = null

    // Playback state
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _bpm = MutableStateFlow(90)
    val bpm: StateFlow<Int> = _bpm.asStateFlow()

    private val _currentBar = MutableStateFlow(1)
    val currentBar: StateFlow<Int> = _currentBar.asStateFlow()

    private val _currentBeat = MutableStateFlow(1)
    val currentBeat: StateFlow<Int> = _currentBeat.asStateFlow()

    private val _currentStep = MutableStateFlow(0)
    val currentStep: StateFlow<Int> = _currentStep.asStateFlow()

    private val _activePattern = MutableStateFlow(DrumPattern.HIP_HOP)
    val activePattern: StateFlow<DrumPattern> = _activePattern.asStateFlow()

    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

    private val _masterVolume = MutableStateFlow(1.0f)
    val masterVolume: StateFlow<Float> = _masterVolume.asStateFlow()

    // Per-instrument volume adjustments
    private val instrumentVolumes = DrumSound.entries.associateWith { 1.0f }.toMutableMap()

    // Pre-synthesized DSP PCM drum sample buffers (at 44.1 kHz, 16-bit mono)
    private val kickSample: ShortArray by lazy { generateKick(SAMPLE_RATE, 0.28, 145.0, 42.0) }
    private val sub808Sample: ShortArray by lazy { generateSub808(SAMPLE_RATE, 0.45, 120.0, 38.0) }
    private val snareSample: ShortArray by lazy { generateSnare(SAMPLE_RATE, 0.18, 185.0) }
    private val clapSample: ShortArray by lazy { generateClap(SAMPLE_RATE, 0.16) }
    private val hihatClosedSample: ShortArray by lazy { generateHiHat(SAMPLE_RATE, 0.05, 0.6) }
    private val hihatOpenSample: ShortArray by lazy { generateHiHat(SAMPLE_RATE, 0.18, 0.8) }
    private val tomSample: ShortArray by lazy { generateTom(SAMPLE_RATE, 0.25, 110.0, 65.0) }
    private val rimshotSample: ShortArray by lazy { generateRimshot(SAMPLE_RATE, 0.06) }

    /**
     * Set tempo in Beats Per Minute (clamped between 40 and 240).
     */
    fun setBpm(newBpm: Int) {
        val clamped = newBpm.coerceIn(40, 240)
        _bpm.value = clamped
    }

    /**
     * Assign a 16-step rhythm pattern to the sequencer.
     */
    fun setPattern(pattern: DrumPattern) {
        _activePattern.value = pattern
    }

    /**
     * Toggle a specific step on or off for an instrument.
     */
    fun toggleStep(sound: DrumSound, step: Int) {
        _activePattern.value = _activePattern.value.copyWithToggledStep(sound, step)
    }

    /**
     * Set volume for an individual percussive instrument (0.0 to 1.0).
     */
    fun setInstrumentVolume(sound: DrumSound, volume: Float) {
        instrumentVolumes[sound] = volume.coerceIn(0f, 1f)
    }

    /**
     * Set master playback volume (0.0 to 1.0).
     */
    fun setMasterVolume(volume: Float) {
        _masterVolume.value = volume.coerceIn(0f, 1f)
    }

    /**
     * Toggle mute state.
     */
    fun toggleMute() {
        _isMuted.value = !_isMuted.value
    }

    /**
     * Toggle between playback and stop.
     */
    fun togglePlay() {
        if (_isPlaying.value) {
            stop()
        } else {
            start()
        }
    }

    /**
     * Start the AudioTrack audio stream and step sequencer.
     */
    fun start() {
        if (playbackJob?.isActive == true) return

        try {
            val frameSizeInBytes = 2 // 16-bit mono PCM = 2 bytes per frame
            val minBuf = AudioTrack.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val baseSize = if (minBuf > 0) minBuf.coerceAtLeast(8192) else 8192
            val bufferSize = if (baseSize % frameSizeInBytes != 0) {
                baseSize + (frameSizeInBytes - (baseSize % frameSizeInBytes))
            } else {
                baseSize
            }

            audioTrack = AudioTrack.Builder()
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
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack?.play()
            _isPlaying.value = true
            _currentBar.value = 1
            _currentBeat.value = 1
            _currentStep.value = 0

            playbackJob = scope.launch(Dispatchers.Default) {
                sequenceLoop()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize AudioTrack for DrumBeatEngine", e)
            _isPlaying.value = false
        }
    }

    /**
     * Stop sequencing and release AudioTrack resources.
     */
    fun stop() {
        playbackJob?.cancel()
        playbackJob = null
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping AudioTrack", e)
        }
        audioTrack = null
        _isPlaying.value = false
        _currentStep.value = 0
        _currentBeat.value = 1
        _currentBar.value = 1
    }

    /**
     * Main sequencing loop: calculates step durations dynamically based on BPM
     * and streams mixed audio buffers into AudioTrack.
     */
    private suspend fun sequenceLoop() {
        var step = 0
        var bar = 1

        while (scope.isActive) {
            val currentBpm = _bpm.value
            val muted = _isMuted.value
            val masterVol = _masterVolume.value
            val pattern = _activePattern.value

            // 16th note step duration in seconds: (60 / BPM) / 4
            val stepDurationSec = (60.0 / currentBpm) / 4.0
            val stepSampleCount = (stepDurationSec * SAMPLE_RATE).toInt().coerceAtLeast(1)

            // Current beat (1..4) in 4/4 time
            val beat = (step / 4) + 1

            _currentBar.value = bar
            _currentBeat.value = beat
            _currentStep.value = step

            // Compose output buffer for this step
            val stepBuffer = ShortArray(stepSampleCount)

            if (!muted && masterVol > 0f) {
                // Mix each active instrument for this step
                for (sound in DrumSound.entries) {
                    if (pattern.isStepActive(sound, step)) {
                        val sample = getSampleForSound(sound)
                        val instVol = (instrumentVolumes[sound] ?: 1.0f) * masterVol
                        mixSampleIntoBuffer(sample, stepBuffer, instVol)
                    }
                }
            }

            // Write PCM audio buffer to AudioTrack
            audioTrack?.let { track ->
                if (track.playState == AudioTrack.PLAYSTATE_PLAYING) {
                    track.write(stepBuffer, 0, stepBuffer.size)
                }
            }

            // Advance to next 16th note step
            step = (step + 1) % DEFAULT_STEPS_PER_BAR
            if (step == 0) {
                bar++
                if (bar > 64) bar = 1
            }
        }
    }

    private fun getSampleForSound(sound: DrumSound): ShortArray {
        return when (sound) {
            DrumSound.KICK -> kickSample
            DrumSound.SNARE -> snareSample
            DrumSound.HI_HAT_CLOSED -> hihatClosedSample
            DrumSound.HI_HAT_OPEN -> hihatOpenSample
            DrumSound.CLAP -> clapSample
            DrumSound.SUB_808 -> sub808Sample
            DrumSound.TOM -> tomSample
            DrumSound.RIMSHOT -> rimshotSample
        }
    }

    private fun mixSampleIntoBuffer(source: ShortArray, target: ShortArray, volume: Float) {
        val count = minOf(source.size, target.size)
        for (i in 0 until count) {
            val mixed = target[i] + (source[i] * volume).toInt()
            target[i] = mixed.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
    }

    // =========================================================================
    // Procedural DSP Drum Sound Synthesizers (PCM 16-bit, 44.1 kHz Mono)
    // =========================================================================

    private fun generateKick(rate: Int, durationSec: Double, startFreq: Double, endFreq: Double): ShortArray {
        val samples = (durationSec * rate).toInt()
        val data = ShortArray(samples)
        for (i in 0 until samples) {
            val t = i.toDouble() / rate
            val progress = (t / durationSec).coerceIn(0.0, 1.0)
            val freq = startFreq * exp(-progress * 9.0) + endFreq
            val envelope = exp(-progress * 6.5)
            val sample = sin(2.0 * PI * freq * t) * envelope
            data[i] = (sample * Short.MAX_VALUE * 0.95).toInt().toShort()
        }
        return data
    }

    private fun generateSub808(rate: Int, durationSec: Double, startFreq: Double, endFreq: Double): ShortArray {
        val samples = (durationSec * rate).toInt()
        val data = ShortArray(samples)
        for (i in 0 until samples) {
            val t = i.toDouble() / rate
            val progress = (t / durationSec).coerceIn(0.0, 1.0)
            val freq = startFreq * exp(-progress * 6.0) + endFreq
            val envelope = exp(-progress * 3.8)
            val sample = sin(2.0 * PI * freq * t) * envelope
            data[i] = (sample * Short.MAX_VALUE * 0.98).toInt().toShort()
        }
        return data
    }

    private fun generateSnare(rate: Int, durationSec: Double, bodyFreq: Double): ShortArray {
        val samples = (durationSec * rate).toInt()
        val data = ShortArray(samples)
        val random = Random(42)
        for (i in 0 until samples) {
            val t = i.toDouble() / rate
            val progress = (t / durationSec).coerceIn(0.0, 1.0)
            val bodyEnv = exp(-progress * 18.0)
            val body = sin(2.0 * PI * bodyFreq * t) * bodyEnv * 0.45
            val noiseEnv = exp(-progress * 10.0)
            val noise = (random.nextDouble() * 2.0 - 1.0) * noiseEnv * 0.55
            val sample = (body + noise).coerceIn(-1.0, 1.0)
            data[i] = (sample * Short.MAX_VALUE * 0.9).toInt().toShort()
        }
        return data
    }

    private fun generateClap(rate: Int, durationSec: Double): ShortArray {
        val samples = (durationSec * rate).toInt()
        val data = ShortArray(samples)
        val random = Random(1337)
        for (i in 0 until samples) {
            val t = i.toDouble() / rate
            val burst1 = if (t < 0.012) exp(-t / 0.003) else 0.0
            val burst2 = if (t in 0.012..0.024) exp(-(t - 0.012) / 0.003) else 0.0
            val burst3 = if (t >= 0.024) exp(-(t - 0.024) / 0.04) else 0.0
            val envelope = (burst1 * 0.7 + burst2 * 0.8 + burst3 * 1.0).coerceIn(0.0, 1.0)
            val noise = (random.nextDouble() * 2.0 - 1.0) * envelope
            data[i] = (noise * Short.MAX_VALUE * 0.85).toInt().toShort()
        }
        return data
    }

    private fun generateHiHat(rate: Int, durationSec: Double, volume: Double): ShortArray {
        val samples = (durationSec * rate).toInt()
        val data = ShortArray(samples)
        val random = Random(999)
        var lastNoise = 0.0
        for (i in 0 until samples) {
            val progress = i.toDouble() / samples
            val envelope = exp(-progress * 22.0)
            val raw = random.nextDouble() * 2.0 - 1.0
            val highPass = raw - lastNoise
            lastNoise = raw
            val sample = highPass * envelope * volume
            data[i] = (sample * Short.MAX_VALUE * 0.7).toInt().toShort()
        }
        return data
    }

    private fun generateTom(rate: Int, durationSec: Double, startFreq: Double, endFreq: Double): ShortArray {
        val samples = (durationSec * rate).toInt()
        val data = ShortArray(samples)
        for (i in 0 until samples) {
            val t = i.toDouble() / rate
            val progress = (t / durationSec).coerceIn(0.0, 1.0)
            val freq = startFreq * exp(-progress * 7.0) + endFreq
            val envelope = exp(-progress * 8.0)
            val sample = sin(2.0 * PI * freq * t) * envelope
            data[i] = (sample * Short.MAX_VALUE * 0.9).toInt().toShort()
        }
        return data
    }

    private fun generateRimshot(rate: Int, durationSec: Double): ShortArray {
        val samples = (durationSec * rate).toInt()
        val data = ShortArray(samples)
        val random = Random(777)
        for (i in 0 until samples) {
            val t = i.toDouble() / rate
            val progress = (t / durationSec).coerceIn(0.0, 1.0)
            val click = sin(2.0 * PI * 1800.0 * t) * exp(-progress * 45.0) * 0.6
            val noise = (random.nextDouble() * 2.0 - 1.0) * exp(-progress * 30.0) * 0.4
            val sample = (click + noise).coerceIn(-1.0, 1.0)
            data[i] = (sample * Short.MAX_VALUE * 0.85).toInt().toShort()
        }
        return data
    }
}
