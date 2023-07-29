package com.shahtott.sh_musify.ui.main

import android.app.Application
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import android.provider.MediaStore.Audio
import android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.shahtott.sh_musify.common.handler.fetchMusicFromDevice
import com.shahtott.sh_musify.models.AudioModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(private val application: Application) : ViewModel() {

    private val list: MutableList<AudioModel> = mutableListOf()

    private val _audioList: MutableLiveData<MutableList<AudioModel>> = MutableLiveData()
    val audioList: LiveData<MutableList<AudioModel>> = _audioList


    init {

    }


    fun fetchMusic(): ArrayList<AudioModel> = application.fetchMusicFromDevice()


}