package com.shahtott.sh_musify.di

import com.shahtott.sh_musify.data.local.room.MusicDao
import com.shahtott.sh_musify.data.local.room.MusicDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
class MusicModule {

    @Provides
    @ViewModelScoped
    fun provideCirclesDao(roomDatabase: MusicDataBase): MusicDao {
        return roomDatabase.musicDao()
    }
}