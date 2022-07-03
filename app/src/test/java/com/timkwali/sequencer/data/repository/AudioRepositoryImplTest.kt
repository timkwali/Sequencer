package com.timkwali.sequencer.data.repository

import com.timkwali.sequencer.data.local.AudioData
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AudioRepositoryImplTest {
    private val audioData = mockk<AudioData>()
    private val audioRepository = AudioRepositoryImpl(audioData)

    @Before
    fun setUp() {
        val list = listOf(1, 2, 3)
        every { audioData.audioListA } returns list
        every { audioData.imageListA } returns list
        every { audioData.audioListB } returns list
        every { audioData.imageListB } returns list
    }

    @Test
    fun `get audio list a returns list of audio items`() {
        val audioList = audioRepository.getAudioListA()

        assertEquals(false, audioList.isEmpty())
    }

    @Test
    fun `get audio list b returns list of audio items`() {
        val audioList = audioRepository.getAudioListB()

        assertEquals(false, audioList.isEmpty())
    }

    @Test
    fun `get image list a returns list of audio items`() {
        val audioList = audioRepository.getImageListA()

        assertEquals(false, audioList.isEmpty())
    }

    @Test
    fun `get image list b returns list of audio items`() {
        val audioList = audioRepository.getImageListB()

        assertEquals(false, audioList.isEmpty())
    }
}