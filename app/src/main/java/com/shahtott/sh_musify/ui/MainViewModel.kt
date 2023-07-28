package com.shahtott.sh_musify.ui

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import android.provider.MediaStore.Audio
import android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shahtott.sh_musify.models.AudioModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val application: Application) : ViewModel() {

    private val list: MutableList<AudioModel> = mutableListOf()

    private val _audioList: MutableLiveData<MutableList<AudioModel>> = MutableLiveData()
    val audioList: LiveData<MutableList<AudioModel>> = _audioList


    init {

    }


    fun fetchMusic(): ArrayList<AudioModel> {
        val musicList = ArrayList<AudioModel>()
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA
        )
        val selection = "${Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${Audio.Media.TITLE} ASC"

        val cursor = application.applicationContext.contentResolver.query(
            EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            sortOrder
        )

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(Audio.Media._ID)
            val titleColumn = it.getColumnIndexOrThrow(Audio.Media.TITLE)
            val artistColumn = it.getColumnIndexOrThrow(Audio.Media.ARTIST)
            val dataColumn = it.getColumnIndexOrThrow(Audio.Media.DATA)


            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val title = it.getString(titleColumn)
                val artist = it.getString(artistColumn)
                val data = it.getString(dataColumn)

                val mediaFile = File(data)
                if (!mediaFile.path.contains("WhatsApp/Media") ) {
                Log.e("MusicFetch", "ID: $id, Title: $title, Artist: $artist, Data: $data")
                musicList.add(AudioModel(id, data, title, artist))
            }
            }
        }
        _audioList.value = musicList
        return musicList
    }

}