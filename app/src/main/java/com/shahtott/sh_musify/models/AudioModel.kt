package com.shahtott.sh_musify.models

data class AudioModel(
    val id: Long,
    val data: String,
    val title: String,
    val artist: String,
   // val albumArt: String,
    val duration: String="00:00",
)
