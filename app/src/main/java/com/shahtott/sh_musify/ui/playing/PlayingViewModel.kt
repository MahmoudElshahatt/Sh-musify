package com.shahtott.sh_musify.ui.playing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.shahtott.sh_musify.data.local.room.MusicEntity
import com.shahtott.sh_musify.domain.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayingViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _musicEntity: MutableLiveData<MusicEntity> = MutableLiveData()
    val musicEntity: LiveData<MusicEntity> = _musicEntity

    private var songsList = emptyList<MusicEntity>()

    private val musicSongId = savedStateHandle.get<Long>("musicId") ?: 0

    init {
        CoroutineScope(Dispatchers.IO).launch {
            _musicEntity.postValue(mainRepository.getMusicEntityById(musicSongId))
        }
    }

    fun onNextSongClick() {
        CoroutineScope(Dispatchers.IO).launch {
            songsList = mainRepository.getMusic()
            val currentIndex = songsList.indexOf(_musicEntity.value)
            if (currentIndex == songsList.size - 1) return@launch
            _musicEntity.postValue(songsList[currentIndex + 1])
        }
    }

    fun onPrevSongClick() {
        CoroutineScope(Dispatchers.IO).launch {
            songsList = mainRepository.getMusic()
            val currentIndex = songsList.indexOf(_musicEntity.value)
            if (currentIndex == 0) return@launch
            _musicEntity.postValue(songsList[currentIndex - 1])
        }
    }

}