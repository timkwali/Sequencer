package com.timkwali.sequencer.domain.usecase

import com.timkwali.sequencer.domain.repository.AudioRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetAudioDataTest {
    private val audioRepository = mockk<AudioRepository>()
    private val getAudioData = GetAudioData(audioRepository)

    @Before
    fun setUp() {
        val list = listOf(44, 32, 87)
        every { audioRepository.getAudioListA() } returns list
        every { audioRepository.getImageListA() } returns list
        every { audioRepository.getAudioListB() } returns list
        every { audioRepository.getImageListB() } returns list
    }

    @Test
    fun `get audio data returns correct audio resource`() {
        val audioDataList = getAudioData(1, 2)

        assertEquals(87, audioDataList[1].audioResource)
    }

    @Test
    fun `get audio data returns correct image resource`() {
        val audioDataList = getAudioData(0, 2)

        assertEquals(44, audioDataList[0].imageResource)
    }
}