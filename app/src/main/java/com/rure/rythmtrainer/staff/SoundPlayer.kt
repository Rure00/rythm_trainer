package com.rure.rythmtrainer.staff

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.util.Log
import androidx.compose.runtime.remember
import com.rure.rythmtrainer.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

class SoundPlayer(
    private val context: Context,
    private val bpm: Int,
    private val noteNum: Int,
    private val headNote: Note,
    private val notes: List<Note>
) {
    private val tag = "SoundPlayer"

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

    // headNote 하나 당 시간
    private val secPerHeadNote = 240 / (headNote.beat * bpm)
    // 한 마디가 가지는 시간
    private val secPerNode = secPerHeadNote * noteNum

    private var soundId = 0
    var isPlaying = false
        private set
    var isReady = false
        private set

    init {
        soundId = soundPool.load(context, R.raw.sol, 1);
        soundPool.setOnLoadCompleteListener { it, _, _ ->
            Log.d(tag, "load success.")
            isReady = true
        }
    }

    private val dispatcher = Dispatchers.IO



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

            var index = 0
            var delaySum = 0F
            while (isPlaying) {
                Log.d(tag, "index: $index/${notes.size - 1}")
                val note = notes[index]
                val delay = secPerHeadNote * (headNote.beat.toFloat() / note.beat.toFloat()) * 1000
                Log.d(tag, "delay: ${delay}")
                delaySum += delay

                val gap = (System.currentTimeMillis() - (timeStamp + delay) ) * 1000
                soundPool.play(soundId, volume, volume, 1, 0 ,1f)

                index++
                delay(delay.toLong() - gap.toLong())

                if(index == notes.size) {
                    stop()
                    break
                }
            }
        }

        val result = work?.start() ?: false
        Log.d(tag, "work: $result")
        return result
    }
}