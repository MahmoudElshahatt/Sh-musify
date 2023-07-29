package com.shahtott.sh_musify.models

data class AudioModel(
    val id: Long,
    val data: String,
    val title: String,
    val duration: Int,
    val artist: String,
    val albumArt: Int,
)
