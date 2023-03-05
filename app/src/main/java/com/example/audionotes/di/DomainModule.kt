package com.example.audionotes.di

import com.example.domain.db.DbRepository
import com.example.domain.record.IPlayer
import com.example.domain.record.IRecorderRepository
import com.example.domain.usecase.LoadRecordsUseCase
import com.example.domain.usecase.PlayAudioUseCase
import com.example.domain.usecase.RecordAudioUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class DomainModule {

    @Provides
    fun provideRecordAudioMessageUseCase(repository: IRecorderRepository): RecordAudioUseCase {
        return RecordAudioUseCase(repository)
    }

    @Provides
    fun providePlayMusicUseCase(player: IPlayer): PlayAudioUseCase {
        return PlayAudioUseCase(player)
    }

    @Provides
    fun provideGetRecordsUseCase(dbRepository: DbRepository): LoadRecordsUseCase {
        return LoadRecordsUseCase(dbRepository)
    }
}