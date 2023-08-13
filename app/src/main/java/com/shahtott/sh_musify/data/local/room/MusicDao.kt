package com.shahtott.sh_musify.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.util.concurrent.Flow

@Dao
interface MusicDao {

    @Query("SELECT * FROM MusicEntity")
    fun getAllMusic(): List<MusicEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMusic(books: List<MusicEntity>)

    @Query("DELETE FROM MusicEntity")
    fun deleteMusic()

    @Query("SELECT * FROM MusicEntity WHERE id=:id")
    fun getMusicEntityById(id: Long): MusicEntity
}