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
        mediaPlayer = MediaPlayer.create(context, audio)
        mediaPlayer?.isLooping = false
        mediaPlayer?.setOnCompletionListener {
            completeCallback()
        }
    }

    fun playPauseAudio() {
        isPlaying = if(mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            false
        } else {
            mediaPlayer?.start()
            true
        }
    }

    fun stop() {
        if(mediaPlayer != null) {
            mediaPlayer?.apply {
                stop()
                reset()
                release()
                mediaPlayer = null
            }
            isPlaying = false
        }
    }
}