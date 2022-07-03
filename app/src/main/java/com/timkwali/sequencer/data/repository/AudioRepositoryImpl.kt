package com.timkwali.sequencer.data.repository

import com.timkwali.sequencer.data.local.AudioData
import com.timkwali.sequencer.domain.repository.AudioRepository
import javax.inject.Inject

class AudioRepositoryImpl @Inject constructor(
    private val audioData: AudioData
): AudioRepository {
    override fun getAudioListA(): List<Int> {
        return audioData.audioListA
    }

    override fun getImageListA(): List<Int> {
        return audioData.imageListA
    }

    override fun getAudioListB(): List<Int> {
        return audioData.audioListB
    }

    override fun getImageListB(): List<Int> {
        return audioData.imageListB
    }
}