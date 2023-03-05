package com.example.audionotes.di


import android.content.Context
import androidx.room.Room
import com.example.data.record.record.RecordsRepositoryImpl
import com.example.data.record.db.RecordsDatabase
import com.example.data.record.db.DbMigrations
import com.example.data.record.db.RecordsDao
import com.example.data.record.record.RecorderRepositoryImpl
import com.example.data.record.record.PlayerRepositoryImpl
import com.example.domain.db.DbRepository
import com.example.domain.record.IRecorderRepository
import com.example.domain.record.IPlayer

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {


    @Provides
    @Singleton
    fun provideRetrofit(BASE_URL: String, client: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .baseUrl(BASE_URL)
        .build()

    @Provides
    fun provideRecordsDao(recordsDatabase: RecordsDatabase): RecordsDao {
        return recordsDatabase.recordsDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): RecordsDatabase {
        return Room.databaseBuilder(
            appContext,
            RecordsDatabase::class.java, "AppDB.db")
            .addMigrations(DbMigrations.MIGRATION_1_2)
            .build()
    }

    @Provides
    @Singleton
    fun provideRecorder(@ApplicationContext context: Context): IRecorderRepository {
        return RecorderRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideRecordings(@ApplicationContext context: Context): IPlayer {
        return PlayerRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideDbRepository(dao: RecordsDao): DbRepository {
        return RecordsRepositoryImpl(dao)
    }

    // TODO replace by actual Url
    @Provides
    @Singleton
    fun providesBaseUrl(): String = "VkVk"

}