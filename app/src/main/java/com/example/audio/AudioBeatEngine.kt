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

private const val TAG = "AudioBeatEngine"
private const val SAMPLE_RATE = 44100

enum class Genre(
    val displayName: String,
    val defaultBpm: Int,
    val description: String
) {
    HIP_HOP("Hip-Hop / Boom Bap", 90, "Classic 90s boom-bap swing with hard snare & punchy kick"),
    TRAP("Trap", 140, "Deep 808 sub-bass, rapid hi-hat rolls & crisp snappy claps"),
    POP("Pop / Dance", 120, "Four-on-the-floor rhythmic driving groove & upbeat hi-hats"),
    COUNTRY("Country / Acoustic", 105, "Heartland acoustic kick-and-snare train groove"),
    RNB("R&B / Soul", 85, "Slow sensual groove with syncopated 808s and warm snares"),
    LO_FI("Lo-Fi Chill", 75, "Mellow, dusty relaxed cadence for reflective flows"),
    ROCK("Rock / Alt", 128, "Driving energetic backbeat with steady rock pulse")
}

data class BeatPosition(
    val bar: Int = 1,
    val beat: Int = 1, // 1, 2, 3, 4
    val step: Int = 0, // 0..15
    val isPlaying: Boolean = false,
    val bpm: Int = 90
)

class AudioBeatEngine(private val scope: CoroutineScope) {

    private var audioTrack: AudioTrack? = null
    private var playbackJob: Job? = null

    private val _beatPosition = MutableStateFlow(BeatPosition())
    val beatPosition: StateFlow<BeatPosition> = _beatPosition.asStateFlow()

    private val _currentGenre = MutableStateFlow(Genre.HIP_HOP)
    val currentGenre: StateFlow<Genre> = _currentGenre.asStateFlow()

    private val _bpm = MutableStateFlow(90)
    val bpm: StateFlow<Int> = _bpm.asStateFlow()

    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

    // Tap tempo tracking
    private val tapTimestamps = mutableListOf<Long>()

    // Pre-synthesized drum samples at 44.1kHz (ShortArray)
    private val kickSample: ShortArray by lazy { generateKick(SAMPLE_RATE, 0.28, 145.0, 42.0) }
    private val sub808Sample: ShortArray by lazy { generateSub808(SAMPLE_RATE, 0.45, 120.0, 38.0) }
    private val snareSample: ShortArray by lazy { generateSnare(SAMPLE_RATE, 0.18, 185.0) }
    private val clapSample: ShortArray by lazy { generateClap(SAMPLE_RATE, 0.16) }
    private val hihatClosed: ShortArray by lazy { generateHiHat(SAMPLE_RATE, 0.05, 0.6) }
    private val hihatOpen: ShortArray by lazy { generateHiHat(SAMPLE_RATE, 0.18, 0.8) }

    fun setGenre(genre: Genre) {
        _currentGenre.value = genre
        _bpm.value = genre.defaultBpm
        _beatPosition.value = _beatPosition.value.copy(bpm = genre.defaultBpm)
    }

    fun setBpm(newBpm: Int) {
        val clamped = newBpm.coerceIn(50, 190)
        _bpm.value = clamped
        _beatPosition.value = _beatPosition.value.copy(bpm = clamped)
    }

    fun registerTap() {
        val now = System.currentTimeMillis()
        tapTimestamps.add(now)
        // Keep last 4 taps
        if (tapTimestamps.size > 4) {
            tapTimestamps.removeAt(0)
        }
        if (tapTimestamps.size >= 2) {
            val intervals = mutableListOf<Long>()
            for (i in 1 until tapTimestamps.size) {
                val diff = tapTimestamps[i] - tapTimestamps[i - 1]
                if (diff in 250..2000) { // between 30 and 240 bpm
                    intervals.add(diff)
                }
            }
            if (intervals.isNotEmpty()) {
                val avgInterval = intervals.average()
                val calculatedBpm = (60000.0 / avgInterval).toInt().coerceIn(50, 190)
                setBpm(calculatedBpm)
            }
        }
    }

    fun toggleMute() {
        _isMuted.value = !_isMuted.value
    }

    fun togglePlay() {
        if (_beatPosition.value.isPlaying) {
            stop()
        } else {
            play()
        }
    }

    fun play() {
        if (playbackJob?.isActive == true) return

        try {
            val bufferSize = AudioTrack.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(SAMPLE_RATE / 4)

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
            _beatPosition.value = _beatPosition.value.copy(isPlaying = true, bar = 1, beat = 1, step = 0)

            playbackJob = scope.launch(Dispatchers.Default) {
                streamBeats()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start AudioTrack", e)
            _beatPosition.value = _beatPosition.value.copy(isPlaying = false)
        }
    }

    fun stop() {
        playbackJob?.cancel()
        playbackJob = null
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing audio track", e)
        }
        audioTrack = null
        _beatPosition.value = _beatPosition.value.copy(isPlaying = false, bar = 1, beat = 1, step = 0)
    }

    private suspend fun streamBeats() {
        var currentStep = 0
        var currentBar = 1

        while (scope.isActive) {
            val targetBpm = _bpm.value
            val genre = _currentGenre.value
            val muted = _isMuted.value

            // 16th note duration in samples
            // 1 beat = 60 / BPM seconds
            // 1 step (16th note) = (60 / BPM) / 4 seconds
            val stepDurationSeconds = (60.0 / targetBpm) / 4.0
            val stepSampleCount = (stepDurationSeconds * SAMPLE_RATE).toInt()

            // Calculate current beat (1..4)
            val beatNumber = (currentStep / 4) + 1

            // Notify UI
            _beatPosition.value = BeatPosition(
                bar = currentBar,
                beat = beatNumber,
                step = currentStep,
                isPlaying = true,
                bpm = targetBpm
            )

            // Compose audio buffer for this step
            val stepBuffer = ShortArray(stepSampleCount)

            if (!muted) {
                mixStepSounds(genre, currentStep, stepBuffer)
            }

            // Stream to AudioTrack
            audioTrack?.let { track ->
                if (track.playState == AudioTrack.PLAYSTATE_PLAYING) {
                    track.write(stepBuffer, 0, stepBuffer.size)
                }
            }

            // Advance step
            currentStep = (currentStep + 1) % 16
            if (currentStep == 0) {
                currentBar++
                if (currentBar > 64) currentBar = 1
            }
        }
    }

    private fun mixStepSounds(genre: Genre, step: Int, buffer: ShortArray) {
        when (genre) {
            Genre.HIP_HOP -> {
                // Classic Boom-Bap: Kick on 0, 7, 10; Snare on 4, 12; Hi-hat on evens
                if (step == 0 || step == 7 || step == 10) mixSample(kickSample, buffer, 1.0f)
                if (step == 4 || step == 12) mixSample(snareSample, buffer, 0.95f)
                if (step % 2 == 0) mixSample(hihatClosed, buffer, 0.5f)
            }
            Genre.TRAP -> {
                // Trap: Heavy 808 on 0, 11; Crisp Clap on 8; rapid hi-hats
                if (step == 0 || step == 11) mixSample(sub808Sample, buffer, 1.0f)
                if (step == 8) mixSample(clapSample, buffer, 0.95f)
                // Trap hi-hat rolls: all evens, plus 16th triplets at step 14, 15
                if (step % 2 == 0 || step == 14 || step == 15) {
                    mixSample(hihatClosed, buffer, if (step >= 14) 0.35f else 0.55f)
                }
            }
            Genre.POP -> {
                // 4-on-the-floor: Kick on 0, 4, 8, 12; Clap/Snare on 4, 12; Offbeat open hi-hat
                if (step % 4 == 0) mixSample(kickSample, buffer, 0.95f)
                if (step == 4 || step == 12) mixSample(clapSample, buffer, 0.85f)
                if (step == 2 || step == 6 || step == 10 || step == 14) {
                    mixSample(hihatOpen, buffer, 0.6f)
                } else if (step % 2 == 0) {
                    mixSample(hihatClosed, buffer, 0.35f)
                }
            }
            Genre.COUNTRY -> {
                // Country train shuffle: Kick on 0, 8; Snare on 4, 12; subtle shuffle hats
                if (step == 0 || step == 8) mixSample(kickSample, buffer, 0.9f)
                if (step == 4 || step == 12) mixSample(snareSample, buffer, 0.85f)
                if (step % 2 == 0 || step == 3 || step == 11) mixSample(hihatClosed, buffer, 0.4f)
            }
            Genre.RNB -> {
                // Smooth R&B: Syncopated kick on 0, 6, 10; Snare on 4, 12; smooth hi-hat
                if (step == 0 || step == 6 || step == 10) mixSample(sub808Sample, buffer, 0.85f)
                if (step == 4 || step == 12) mixSample(snareSample, buffer, 0.85f)
                if (step % 2 == 0) mixSample(hihatClosed, buffer, 0.45f)
                if (step == 14) mixSample(hihatOpen, buffer, 0.4f)
            }
            Genre.LO_FI -> {
                // Lo-fi dust: Kick on 0, 7, 10; soft snare on 4, 12; relaxed hi-hat
                if (step == 0 || step == 7 || step == 10) mixSample(kickSample, buffer, 0.8f)
                if (step == 4 || step == 12) mixSample(snareSample, buffer, 0.7f)
                if (step % 2 == 0) mixSample(hihatClosed, buffer, 0.35f)
            }
            Genre.ROCK -> {
                // Driving Rock: Kick on 0, 8, 10; Snare on 4, 12; Straight 8th hi-hats
                if (step == 0 || step == 8 || step == 10) mixSample(kickSample, buffer, 0.95f)
                if (step == 4 || step == 12) mixSample(snareSample, buffer, 0.95f)
                if (step % 2 == 0) mixSample(hihatClosed, buffer, 0.6f)
            }
        }
    }

    private fun mixSample(source: ShortArray, target: ShortArray, volume: Float) {
        val limit = minOf(source.size, target.size)
        for (i in 0 until limit) {
            val mixed = target[i] + (source[i] * volume).toInt()
            target[i] = mixed.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
    }

    // Procedural sound generators
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
            val progress = (t / durationSec).coerceIn(0.0, 1.0)
            // Multiple micro-bursts at 0ms, 12ms, 24ms
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
            // High-pass filter simulation
            val raw = random.nextDouble() * 2.0 - 1.0
            val highPass = raw - lastNoise
            lastNoise = raw
            val sample = highPass * envelope * volume
            data[i] = (sample * Short.MAX_VALUE * 0.7).toInt().toShort()
        }
        return data
    }
}
