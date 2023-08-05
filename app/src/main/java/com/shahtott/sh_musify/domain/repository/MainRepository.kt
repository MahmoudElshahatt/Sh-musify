package com.shahtott.sh_musify.domain.repository

import android.app.Application
import com.shahtott.sh_musify.common.handler.MusicHandler.fetchMusicFromDevice
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepository @Inject constructor(
    private val application: Application,
) {
    fun fetchMusicFromDevice() = application.fetchMusicFromDevice()
}