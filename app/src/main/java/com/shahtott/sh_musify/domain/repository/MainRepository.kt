package com.shahtott.sh_musify.domain.repository

import android.app.Application
import com.shahtott.sh_musify.common.core.BaseCacheStrategy
import com.shahtott.sh_musify.common.handler.AudioModel
import com.shahtott.sh_musify.common.handler.MusicHandler.fetchMusicFromDevice
import com.shahtott.sh_musify.common.handler.toLocalMusicEntities
import com.shahtott.sh_musify.data.local.SharedPrefManager
import com.shahtott.sh_musify.data.local.room.MusicDao
import com.shahtott.sh_musify.data.local.room.MusicEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


class MainRepository @Inject constructor(
    private val application: Application,
    private val musicDao: MusicDao
) : BaseCacheStrategy<AudioModel, MusicEntity> {
    @Inject
    lateinit var sharedPref: SharedPrefManager

    suspend fun getMusic(): List<MusicEntity> =
        withContext(Dispatchers.IO) {
            getData()
        }


    override suspend fun getFromCache(page: Int, pageSize: Int)
            : List<MusicEntity> {
        return musicDao.getAllMusic()
    }

    override suspend fun clearCachedData() {
        musicDao.deleteMusic()
    }

    override suspend fun saveToCache(page: Int, pageSize: Int, data: List<MusicEntity>) {
        musicDao.insertMusic(data)
    }

    override fun mapFromRemoteToLocal(remoteData: List<AudioModel>): List<MusicEntity> {
        return remoteData.toLocalMusicEntities(application)
    }

    override suspend fun fetchFromRemote(page: Int, pageSize: Int): List<AudioModel> {
        return application.fetchMusicFromDevice()
    }

    override suspend fun getLastSaveTime() =
        sharedPref.read(SharedPrefManager.LAST_FETCH, 0L)

    override suspend fun updateLastSaveTime(timeStamp: Long) {
        sharedPref.write(SharedPrefManager.LAST_FETCH, timeStamp)
    }
}