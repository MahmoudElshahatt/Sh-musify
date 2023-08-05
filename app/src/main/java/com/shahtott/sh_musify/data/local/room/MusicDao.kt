package com.shahtott.sh_musify.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MusicDao {

    @Query("SELECT * FROM MusicEntity")
    fun getAllBooks(): List<MusicEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBooks(books: List<MusicEntity>)

    @Query("DELETE FROM MusicEntity")
    fun deleteBooks()
}