package com.shahtott.sh_musify.data.local.room

import androidx.room.ColumnInfo
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
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val imageBytes: ByteArray = byteArrayOf(),
    val isPlaying: Boolean = false,
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MusicEntity

        if (id != other.id) return false
        if (data != other.data) return false
        if (title != other.title) return false
        if (artist != other.artist) return false
        if (duration != other.duration) return false
        if (imageBytes != null) {
            if (other.imageBytes == null) return false
            if (!imageBytes.contentEquals(other.imageBytes)) return false
        } else if (other.imageBytes != null) return false
        if (isPlaying != other.isPlaying) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + data.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + artist.hashCode()
        result = 31 * result + duration.hashCode()
        result = 31 * result + (imageBytes?.contentHashCode() ?: 0)
        result = 31 * result + isPlaying.hashCode()
        return result
    }

}
