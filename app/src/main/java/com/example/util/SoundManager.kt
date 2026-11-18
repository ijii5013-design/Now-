package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

/**
 * Procedural Minecraft-style sound effects & haptic feedback generator.
 * Uses AudioTrack with PCM audio synthesis for zero asset overhead.
 */
class SoundManager(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.Default)

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        manager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    /**
     * Vibrate briefly for tactile feedback
     */
    fun vibrate(durationMs: Long = 20, intensity: Int = 100) {
        try {
            vibrator?.let { v ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(durationMs, intensity.coerceIn(1, 255)))
                } else {
                    @Suppress("DEPRECATION")
                    v.vibrate(durationMs)
                }
            }
        } catch (_: Exception) {}
    }

    /**
     * Minecraft UI click sound
     */
    fun playClick() {
        vibrate(15, 80)
        scope.launch {
            val sampleRate = 44100
            val durationMs = 35
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val decay = 1.0 - (i.toDouble() / numSamples)
                val tone = sin(2.0 * PI * 1800.0 * t) * 0.4 +
                        sin(2.0 * PI * 900.0 * t) * 0.3 +
                        (Random.nextDouble(-0.3, 0.3) * 0.3)
                buffer[i] = (tone * decay * Short.MAX_VALUE).toInt().toShort()
            }
            playPcm(buffer, sampleRate)
        }
    }

    /**
     * Minecraft Level-Up Ding / XP Arpeggio (C5 -> E5 -> G5 -> C6)
     */
    fun playLevelUp() {
        vibrate(40, 150)
        scope.launch {
            val sampleRate = 44100
            val noteDurationMs = 70
            val notes = doubleArrayOf(523.25, 659.25, 783.99, 1046.50)
            val noteSamples = (sampleRate * (noteDurationMs / 1000.0)).toInt()
            val totalSamples = noteSamples * notes.size
            val buffer = ShortArray(totalSamples)

            for (n in notes.indices) {
                val freq = notes[n]
                for (i in 0 until noteSamples) {
                    val idx = n * noteSamples + i
                    val t = i.toDouble() / sampleRate
                    val decay = (1.0 - (i.toDouble() / noteSamples)).coerceAtLeast(0.0)
                    val tone = sin(2.0 * PI * freq * t) * 0.7 +
                            sin(2.0 * PI * freq * 2.0 * t) * 0.25
                    buffer[idx] = (tone * decay * Short.MAX_VALUE * 0.75).toInt().toShort()
                }
            }
            playPcm(buffer, sampleRate)
        }
    }

    /**
     * Block break crunchy crunch sound
     */
    fun playBlockBreak() {
        vibrate(30, 180)
        scope.launch {
            val sampleRate = 44100
            val durationMs = 65
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val decay = 1.0 - (i.toDouble() / numSamples)
                val noise = Random.nextDouble(-1.0, 1.0)
                val lowRumble = sin(2.0 * PI * 140.0 * (i.toDouble() / sampleRate))
                val wave = (noise * 0.7 + lowRumble * 0.3) * decay
                buffer[i] = (wave * Short.MAX_VALUE * 0.7).toInt().toShort()
            }
            playPcm(buffer, sampleRate)
        }
    }

    /**
     * TNT explosion boom with low frequency rumble
     */
    fun playExplosion() {
        vibrate(120, 255)
        scope.launch {
            val sampleRate = 44100
            val durationMs = 260
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val decay = (1.0 - (i.toDouble() / numSamples)).let { it * it }
                val subBass = sin(2.0 * PI * 65.0 * t) * 0.6
                val midNoise = Random.nextDouble(-0.7, 0.7) * 0.4
                val wave = (subBass + midNoise) * decay
                buffer[i] = (wave * Short.MAX_VALUE * 0.9).toInt().toShort()
            }
            playPcm(buffer, sampleRate)
        }
    }

    /**
     * 2,000,000 FPS Turbo Booster whoosh effect
     */
    fun playTurboBoost() {
        vibrate(60, 200)
        scope.launch {
            val sampleRate = 44100
            val durationMs = 200
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val frac = i.toDouble() / numSamples
                val currentFreq = 300.0 + (frac * 1400.0) // upward pitch sweep
                val t = i.toDouble() / sampleRate
                val envelope = sin(PI * frac) // smooth in/out
                val tone = sin(2.0 * PI * currentFreq * t) * 0.5 +
                        sin(2.0 * PI * (currentFreq * 1.5) * t) * 0.2 +
                        Random.nextDouble(-0.2, 0.2)
                buffer[i] = (tone * envelope * Short.MAX_VALUE * 0.8).toInt().toShort()
            }
            playPcm(buffer, sampleRate)
        }
    }

    private fun playPcm(pcm: ShortArray, sampleRate: Int) {
        try {
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(pcm.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(pcm, 0, pcm.size)
            audioTrack.play()
            // Clean up when finished
            scope.launch {
                kotlinx.coroutines.delay((pcm.size * 1000L / sampleRate) + 50L)
                try {
                    audioTrack.stop()
                    audioTrack.release()
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {}
    }
}
