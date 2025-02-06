package com.rure.rythmtrainer

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class Metronome(
    private val context: Context,
    val bpm: Int
) {
    private val tag = "Metronome"

    var isPlaying = false
    private var soundId = 0

    private val audioManager by lazy {
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }
    private val soundPool by lazy {
        SoundPool.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setMaxStreams(10)
            .build()
    }

    private val dispatcher = Dispatchers.IO
    private val work = CoroutineScope(dispatcher).async(start = CoroutineStart.LAZY) {
        Log.d(tag, "play!")
        val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat() / max
        while (isPlaying) {
            soundPool.play(soundId, volume, volume, 1, 0 ,1f)
            delay(60L / bpm.toLong() * 1000)
        }
    }

    init {
        soundId = soundPool.load(context, R.raw.tick, 1);

        soundPool.setOnLoadCompleteListener { it, _, _ ->
            Log.d(tag, "load success.")
            play()
        }
    }

    fun play() {
        isPlaying = true
        work.start()
    }

    fun stop() {
        kotlin.runCatching {
            isPlaying = false
            work.cancel()
        }.onFailure {
            Log.e(tag, "Metronome Stop Error: ${it.message}")
        }
    }
}