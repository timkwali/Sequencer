package com.timkwali.sequencer.util

import android.content.Context
import android.media.MediaPlayer
import android.util.Log

class AudioPlayer(
    private val context: Context,
    private val completeCallback: () -> Unit
) {

    var mediaPlayer: MediaPlayer? = null
    var isPlaying = false

    fun setUpMediaPlayer(audio: Int) {
//        if(mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, audio)
            mediaPlayer?.isLooping = false
            mediaPlayer?.setOnCompletionListener {
                completeCallback()
            }
            Log.d("mainViewModel", "id: ${mediaPlayer!!.audioSessionId}")
//            initialiseSeekbar()
//        }
    }

    fun playPauseAudio() {
        if(mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            isPlaying = false
        } else {
            mediaPlayer?.start()
            isPlaying = true
        }
    }

    private fun stop() {
        if(mediaPlayer != null) {
            mediaPlayer?.apply {
                stop()
                reset()
                release()
                mediaPlayer = null
            }
        }
    }
}