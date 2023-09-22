package com.shahtott.sh_musify.di

import android.app.Application
import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.room.Room
import com.shahtott.sh_musify.common.handler.MainAudioPlayer
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

    @Provides
    @Singleton
    fun provideExoPLayer(@ApplicationContext context: Context): ExoPlayer {
        return ExoPlayer.Builder(context).build()
    }

    @Provides
    @Singleton
    fun provideMediaSession(
        @ApplicationContext context: Context,
        exoPlayer: ExoPlayer
    ): MediaSession {
        return MediaSession.Builder(context, exoPlayer).build()
    }

    @Provides
    @Singleton
    fun provideMyPlayer(player: ExoPlayer): MainAudioPlayer {
        return MainAudioPlayer(player)
    }



}