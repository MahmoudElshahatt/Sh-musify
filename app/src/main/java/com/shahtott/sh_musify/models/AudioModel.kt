package com.shahtott.sh_musify.models

data class AudioModel(
    val id: Long,
    val data: String,
    val title: String,
    val artist: String,
    val duration: String="00:00",
    val isPlaying:Boolean =false,
)
