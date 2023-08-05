package com.shahtott.sh_musify.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shahtott.sh_musify.common.handler.AudioModel
import com.shahtott.sh_musify.data.local.room.MusicEntity
import com.shahtott.sh_musify.domain.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository,
) : ViewModel() {

    private val _audioList: MutableLiveData<List<MusicEntity>> = MutableLiveData()
    val audioList: LiveData<List<MusicEntity>> = _audioList


    init {

    }


    fun fetchMusic() {
        viewModelScope.launch {
            _audioList.postValue(mainRepository.getMusic())
        }
    }

}