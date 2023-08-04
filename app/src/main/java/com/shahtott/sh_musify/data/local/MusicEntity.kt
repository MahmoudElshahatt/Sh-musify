package com.shahtott.sh_musify.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MusicEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val data: String,
    val title: String,
    val artist: String,
    val duration: String = "00:00",
    val imageUrl: String = "",
    val isPlaying: Boolean = false,
)
