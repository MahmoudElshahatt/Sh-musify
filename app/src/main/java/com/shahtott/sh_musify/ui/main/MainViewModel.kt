package com.shahtott.sh_musify.ui.main

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shahtott.sh_musify.common.handler.AudioModel
import com.shahtott.sh_musify.common.handler.MusicHandler.fetchMusicFromDevice
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor
    (private val application: Application) : ViewModel() {

    private val list: MutableList<AudioModel> = mutableListOf()

    private val _audioList: MutableLiveData<MutableList<AudioModel>> = MutableLiveData()
    val audioList: LiveData<MutableList<AudioModel>> = _audioList


    init {

    }


    fun fetchMusic() {
        _audioList.postValue(application.fetchMusicFromDevice())
    }

}