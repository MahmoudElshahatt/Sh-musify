package com.shahtott.sh_musify.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.shahtott.sh_musify.data.local.room.MusicDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideContext(application: Application): Context =
        application.applicationContext

    @Provides
    @Singleton
    fun provideRoomDb(@ApplicationContext appContext: Context):
            MusicDataBase {
        return Room.databaseBuilder(
            appContext,
            MusicDataBase::class.java,
            "music-db"
        ).fallbackToDestructiveMigration().build()
    }


}