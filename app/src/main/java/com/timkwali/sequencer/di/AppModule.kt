package com.timkwali.sequencer.di

import com.timkwali.sequencer.data.local.AudioData
import com.timkwali.sequencer.data.repository.AudioRepositoryImpl
import com.timkwali.sequencer.domain.repository.AudioRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAudioRepository(audioData: AudioData): AudioRepository {
        return AudioRepositoryImpl(audioData)
    }
}