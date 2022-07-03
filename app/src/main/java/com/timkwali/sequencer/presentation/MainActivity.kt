package com.timkwali.sequencer.presentation

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.timkwali.sequencer.R
import com.timkwali.sequencer.databinding.ActivityMainBinding
import com.timkwali.sequencer.util.AudioTrack
import com.timkwali.sequencer.util.Utils.blink
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
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
                        loopBtn.isEnabled = true
                        loopBtn.setImageResource(R.drawable.ic__loop)
                        microLoopIndicator.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.black))
                    } else {
                        playBtn.setImageResource(R.drawable.ic_play)
                        loopBtn.isEnabled = false
                        loopBtn.setImageResource(R.drawable.ic_loop_grey)
                        microLoopIndicator.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.grey))
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
            lifecycleScope.launchWhenStarted {
                mainViewModel.mtiData.collectLatest {
                    mti.text = it
                }
            }
            lifecycleScope.launchWhenStarted {
                mainViewModel.beatData.collectLatest {
                    if(it.isBeat) beatLed.blink()
                    if(it.isBar) barLed.blink()
                }
            }
            lifecycleScope.launchWhenStarted {
                mainViewModel.isMicroLoopOn.collectLatest {
                    if(it) {
                        microLoopIndicator.text = getString(R.string.on)
                    } else {
                        microLoopIndicator.text = getString(R.string.off)
                    }
                }
            }

            playBtn.setOnClickListener {
                mainViewModel.onPlayClick()
                mainViewModel.updateUiData()
                mainViewModel.setUpBeats()
            }
            playBtn.setOnLongClickListener {
                if(mainViewModel.isPlaying.value) {
                    mainViewModel.reset()
                    playBtn.setImageResource(R.drawable.ic_play)
                }
                true
            }
            nextTrackBtnA.setOnClickListener {
                mainViewModel.nextAudio(AudioTrack.A)
            }
            nextTrackBtnB.setOnClickListener {
                mainViewModel.nextAudio(AudioTrack.B)
            }
            loopBtn.setOnClickListener {
                mainViewModel.switchMicroLoop()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if(mainViewModel.isPlaying.value) mainViewModel.onPlayClick()
    }
}