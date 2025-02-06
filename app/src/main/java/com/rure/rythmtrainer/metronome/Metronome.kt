package com.rure.rythmtrainer.metronome

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.util.Log
import com.rure.rythmtrainer.R
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Metronome(
    private val context: Context,
    val bpm: Int,
) {
    private val tag = "Metronome"

    private val audioManager by lazy {
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    //TODO:
    //  Metronome이 새로 생성되면 기존 soundPool도 계속 재생된다.
    //  따라서, SoundPool을 Object로 관리하거나
    //  Metronome 클래스에서 BPM을 직접 바꿀 수 있도록 해야겠다.
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

    private var soundId = 0
    var isPlaying = false
        private set
    var isReady = false
        private set

    private val dispatcher = Dispatchers.IO

    init {
        soundId = soundPool.load(context, R.raw.tick, 1);
        soundPool.setOnLoadCompleteListener { it, _, _ ->
            Log.d(tag, "load success.")
            isReady = true
        }
    }

    private var work: Job? = null
    fun play(): Boolean {
        if(!isReady) return false

        isPlaying = true
        return doPlay()
    }

    fun stop() {
        if(!isPlaying || work == null) return

        kotlin.runCatching {
            Log.d(tag, "cancel job")
            isPlaying = false
            work?.cancel()
            work = null
        }.onFailure {
            Log.e(tag, "Metronome Stop Error: ${it.message}")
        }
    }

    private fun doPlay(): Boolean {
        work = CoroutineScope(dispatcher).async(start = CoroutineStart.LAZY) {
            val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat() / max

            val timeStamp = System.currentTimeMillis()

            var gap: Long
            val delay = 60_000 / bpm

            Log.d(tag, "delay: ${delay}")

            while (isPlaying) {
                gap = maxOf(1, System.currentTimeMillis() - timeStamp) % delay
                soundPool.play(soundId, volume, volume, 1, 0 ,1f)
                delay(delay - gap)
            }
        }

        val result = work?.start() ?: false
        Log.d(tag, "work: $result")
        return result
    }
}