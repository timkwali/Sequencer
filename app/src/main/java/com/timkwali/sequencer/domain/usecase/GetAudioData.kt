package com.timkwali.sequencer.domain.usecase

import com.timkwali.sequencer.data.AudioData
import com.timkwali.sequencer.domain.model.AudioItem
import javax.inject.Inject

class GetAudioData @Inject constructor(
    private val audioData: AudioData
) {

    operator fun invoke(audioIndexA: Int, audioIndexB: Int): List<AudioItem> {
        val audioItemA = AudioItem(
            audioData.audioListA[audioIndexA],
            audioData.imageListA[audioIndexA],
        )

        val audioItemB = AudioItem(
            audioData.audioListB[audioIndexB],
            audioData.imageListB[audioIndexB],
        )

        return listOf(audioItemA, audioItemB)
    }
}