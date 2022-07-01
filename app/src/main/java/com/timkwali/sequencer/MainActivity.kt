package com.timkwali.sequencer

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        play()
    }

    private fun play() {
        val  music = MediaPlayer.create(this, R.raw.a1)
        music.start();
    }
}