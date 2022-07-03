package com.timkwali.sequencer.domain.repository

interface AudioRepository {

    fun getAudioListA(): List<Int>

    fun getImageListA(): List<Int>

    fun getAudioListB(): List<Int>

    fun getImageListB(): List<Int>
}