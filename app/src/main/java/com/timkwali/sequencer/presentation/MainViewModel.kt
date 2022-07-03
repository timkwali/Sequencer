package com.timkwali.sequencer.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.timkwali.sequencer.domain.model.AudioItem
import com.timkwali.sequencer.domain.usecase.GetAudioData
import com.timkwali.sequencer.presentation.model.BeatData
import com.timkwali.sequencer.presentation.model.SeekbarItem
import com.timkwali.sequencer.util.*
import com.timkwali.sequencer.util.Constants.BPM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    app: Application,
    private val getAudioData: GetAudioData,
    private val mtiCalculator: MTICalculator
): AndroidViewModel(app) {

    private var audioPlayerA: AudioPlayer? = null
    private var audioPlayerB: AudioPlayer? = null

    var currentAudioItemA = MutableStateFlow<AudioItem?>(null)
        private set
    var currentAudioItemB = MutableStateFlow<AudioItem?>(null)
        private set

    private var nextAudioIndexA = 0
    private var nextAudioIndexB = 0

    var isNextEnabledA = MutableStateFlow(true)
        private set
    var isNextEnabledB = MutableStateFlow(true)
        private set

    var isPlaying = MutableStateFlow(false)
        private set

    var seekbarItem: MutableStateFlow<SeekbarItem> = MutableStateFlow(SeekbarItem())
        private set

    var mtiData = MutableStateFlow("0:0:0")
        private set

    var beatData: MutableStateFlow<BeatData> = MutableStateFlow(BeatData())
        private set

    var isMicroLoopOn: MutableStateFlow<Boolean> = MutableStateFlow(false)
        private set

    private var currentTime = 0
    private var beatTime = 0

    private var playbackTimeMillis: MutableStateFlow<Int> = MutableStateFlow(0)
        private set

    private var playbackJob: Job? = null

    init {
        getAudioItems()
        audioPlayerA = AudioPlayer(app.applicationContext) {
            audioPlayerA?.let {
                audioCompleteCallback(AudioTrack.A, it)
            }
        }
        audioPlayerB = AudioPlayer(app.applicationContext) {
            audioPlayerB?.let {
                audioCompleteCallback(AudioTrack.B, it)
            }
        }
        initAudioPlayer()
    }

    private fun initAudioPlayer() {
        val audioA = currentAudioItemA.value?.audioResource
        val audioB = currentAudioItemB.value?.audioResource
        if (audioA != null && audioB != null) {
            audioPlayerA?.setUpMediaPlayer(audioA)
            audioPlayerB?.setUpMediaPlayer(audioB)
        }
    }

    private fun getAudioItems() = viewModelScope.launch {
        val audioItems = getAudioData.invoke(nextAudioIndexA, nextAudioIndexB)
        currentAudioItemA.value = audioItems[0]
        currentAudioItemB.value = audioItems[1]
    }

    private fun getAudioFile(audioTrack: AudioTrack): Int? {
        getAudioItems()
        return when(audioTrack) {
            AudioTrack.A -> currentAudioItemA.value?.audioResource
            AudioTrack.B -> currentAudioItemB.value?.audioResource
        }
    }

    private fun audioCompleteCallback(audioTrack: AudioTrack, audioPlayer: AudioPlayer) {
        when(audioTrack) {
            AudioTrack.A -> {
                isNextEnabledA.value = true
                audioPlayerA?.mediaPlayer?.release()
            }
            AudioTrack.B -> {
                isNextEnabledB.value = true
                audioPlayerB?.mediaPlayer?.release()
            }
        }
        val audio = getAudioFile(audioTrack)
        if(audio != null) {
            audioPlayer.setUpMediaPlayer(audio)
            audioPlayer.playPauseAudio()
            isPlaying.value = audioPlayerA?.isPlaying == true
            updateUiData()
            setUpBeats()
        }
    }

    fun onPlayClick() {
        audioPlayerA?.playPauseAudio()
        audioPlayerB?.playPauseAudio()
        isPlaying.value = audioPlayerA?.isPlaying == true
        if(isMicroLoopOn.value) {
            switchMicroLoop()
        }
    }

    fun updateUiData() {
        viewModelScope.launch {
            while (isPlaying.value) {
                seekbarItem.value = SeekbarItem(
                    audioPlayerA?.mediaPlayer!!.currentPosition,
                    audioPlayerA?.mediaPlayer!!.duration
                )
                playbackTimeMillis.value = audioPlayerA?.mediaPlayer!!.currentPosition
                currentTime++
                mtiCalculator.second = currentTime
                mtiData.value = mtiCalculator.getMTI()
                delay(1)
            }
        }
    }

    fun setUpBeats() {
        viewModelScope.launch {
            while (isPlaying.value) {
                beatTime++
                beatData.value = BeatData(
                    mtiCalculator.isBeat(beatTime),
                    mtiCalculator.isBar(beatTime)
                )
                delay((60000/BPM)/2.toLong())
            }
        }
    }

    fun nextAudio(audioTrack: AudioTrack) {
        when (audioTrack) {
            AudioTrack.A -> {
                if(nextAudioIndexA == 2) {
                    nextAudioIndexA = 0
                } else {
                    nextAudioIndexA++
                }
                isNextEnabledA.value = false
            }
            AudioTrack.B -> {
                if(nextAudioIndexB == 2) {
                    nextAudioIndexB = 0
                } else {
                    nextAudioIndexB++
                }
                isNextEnabledB.value = false
            }
        }
    }

    fun reset() {
        if(isPlaying.value) {
            audioPlayerA?.stop()
            audioPlayerB?.stop()
            isPlaying.value = audioPlayerA?.isPlaying == true
            isMicroLoopOn.value = !isMicroLoopOn.value
            currentTime = 0
            beatTime = 0
            mtiData.value = "0:0:0"
            seekbarItem.value = SeekbarItem()
            initAudioPlayer()
        }
    }

    fun switchMicroLoop() {
        if(isPlaying.value) {
            isMicroLoopOn.value = !isMicroLoopOn.value
        }
        if(isMicroLoopOn.value) {
            val startTime = mtiCalculator.getLoopStartTime()
            val endTime = mtiCalculator.getLoopEndTime()
            startMicroLoop(startTime, endTime)
        } else {
            playbackJob?.cancel()
        }
    }

    private fun startMicroLoop(startTime: Int, endTime: Int) {
        playbackJob = viewModelScope.launch {
            playbackTimeMillis.collectLatest {
                if(isMicroLoopOn.value && playbackTimeMillis.value >= endTime) {
                    audioPlayerA?.mediaPlayer?.seekTo(startTime)
                    audioPlayerB?.mediaPlayer?.seekTo(startTime)
                }
            }
        }
    }
}