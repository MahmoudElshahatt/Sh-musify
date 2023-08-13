package com.shahtott.sh_musify.ui.playing

import androidx.lifecycle.*
import com.shahtott.sh_musify.data.local.room.MusicDao
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

    private val musicSongId = savedStateHandle.get<Long>("musicId") ?: 0

    init {
        CoroutineScope(Dispatchers.IO).launch {
            _musicEntity.postValue(mainRepository.getMusicEntityById(musicSongId))
        }
    }


}