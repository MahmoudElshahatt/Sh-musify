package com.shahtott.sh_musify.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shahtott.sh_musify.common.handler.AudioModel
import com.shahtott.sh_musify.domain.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor
    (
    private val mainRepository: MainRepository,
) : ViewModel() {

    private val _audioList: MutableLiveData<MutableList<AudioModel>> = MutableLiveData()
    val audioList: LiveData<MutableList<AudioModel>> = _audioList


    init {

    }


    fun fetchMusic() {
        _audioList.postValue(mainRepository.fetchMusicFromDevice())
    }

}