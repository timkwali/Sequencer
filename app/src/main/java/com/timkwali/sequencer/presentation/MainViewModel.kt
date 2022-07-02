package com.timkwali.sequencer.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.timkwali.sequencer.domain.model.AudioItem
import com.timkwali.sequencer.domain.usecase.GetAudioData
import com.timkwali.sequencer.util.AudioPlayer
import com.timkwali.sequencer.util.AudioTrack
import com.timkwali.sequencer.util.SeekbarItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val app: Application,
    private val getAudioData: GetAudioData
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
        val audio = getAudioFile(audioTrack)
        if(audio != null) {
            audioPlayer.setUpMediaPlayer(audio)
            audioPlayer.playPauseAudio()
            isPlaying.value = audioPlayerA?.isPlaying == true
            when(audioTrack) {
                AudioTrack.A -> isNextEnabledA.value = true
                AudioTrack.B -> isNextEnabledB.value = true
            }
            setUpSeekbar(audioTrack)
        }
    }

    fun onPlayClick() {
        audioPlayerA?.playPauseAudio()
        audioPlayerB?.playPauseAudio()
        isPlaying.value = audioPlayerA?.isPlaying == true
    }

    fun setUpSeekbar(audioTrack: AudioTrack) {
        viewModelScope.launch {
            val mp = when(audioTrack) {
                AudioTrack.A -> audioPlayerA?.mediaPlayer
                AudioTrack.B -> audioPlayerB?.mediaPlayer
            }
            try {
                while (isPlaying.value) {
                    seekbarItem.value = SeekbarItem(mp!!.currentPosition, mp.duration)
                    delay(1)
                }
            } catch (e: Exception) {
                seekbarItem.value = SeekbarItem(0, 0)
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
}