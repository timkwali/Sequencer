package com.timkwali.sequencer.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.timkwali.sequencer.R
import com.timkwali.sequencer.databinding.ActivityMainBinding
import com.timkwali.sequencer.util.AudioTrack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private val mainViewModel : MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            lifecycleScope.launchWhenStarted {
                mainViewModel.isPlaying.collectLatest {
                    if(it) {
                        playBtn.setImageResource(R.drawable.ic_pause)
                    } else {
                        playBtn.setImageResource(R.drawable.ic_play)
                    }
                }
            }
            lifecycleScope.launchWhenStarted {
                mainViewModel.currentAudioItemA.collectLatest {
                    it?.imageResource?.let { it1 -> imageA.setImageResource(it1) }
                }
            }
            lifecycleScope.launchWhenStarted {
                mainViewModel.currentAudioItemB.collectLatest {
                    it?.imageResource?.let { it1 -> imageB.setImageResource(it1) }
                }
            }
            lifecycleScope.launchWhenStarted {
                mainViewModel.isNextEnabledA.collectLatest {
                    if(it) {
                        nextTrackBtnA.setImageResource(R.drawable.ic_forward)
                        nextTrackBtnA.isEnabled = true
                    } else {
                        nextTrackBtnA.setImageResource(R.drawable.ic_forward_grey)
                        nextTrackBtnA.isEnabled = false
                    }
                }
            }
            lifecycleScope.launchWhenStarted {
                mainViewModel.isNextEnabledB.collectLatest {
                    if(it) {
                        nextTrackBtnB.setImageResource(R.drawable.ic_forward)
                        nextTrackBtnB.isEnabled = true
                    } else {
                        nextTrackBtnB.setImageResource(R.drawable.ic_forward_grey)
                        nextTrackBtnB.isEnabled = false
                    }
                }
            }
            lifecycleScope.launchWhenStarted {
                mainViewModel.seekbarItem.collectLatest {
                    trackProgressA.progress = it.progress
                    trackProgressA.max = it.duration
                    trackProgressB.progress = it.progress
                    trackProgressB.max = it.duration
                }
            }


            playBtn.setOnClickListener {
                mainViewModel.onPlayClick()
                mainViewModel.setUpSeekbar(AudioTrack.A)
                mainViewModel.setUpSeekbar(AudioTrack.B)
            }

            nextTrackBtnA.setOnClickListener {
                mainViewModel.nextAudio(AudioTrack.A)
            }
            nextTrackBtnB.setOnClickListener {
                mainViewModel.nextAudio(AudioTrack.B)
            }

        }
    }

    override fun onPause() {
        super.onPause()
        if(mainViewModel.isPlaying.value) mainViewModel.onPlayClick()
    }
}