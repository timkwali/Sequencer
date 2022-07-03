package com.timkwali.sequencer.domain.usecase

import com.timkwali.sequencer.domain.model.AudioItem
import com.timkwali.sequencer.domain.repository.AudioRepository
import javax.inject.Inject

class GetAudioData @Inject constructor(
    private val audioRepository: AudioRepository
) {

    operator fun invoke(audioIndexA: Int, audioIndexB: Int): List<AudioItem> {
        val audioItemA = AudioItem(
            audioRepository.getAudioListA()[audioIndexA],
            audioRepository.getImageListA()[audioIndexA]
        )

        val audioItemB = AudioItem(
            audioRepository.getAudioListB()[audioIndexB],
            audioRepository.getImageListB()[audioIndexB]
        )

        return listOf(audioItemA, audioItemB)
    }
}