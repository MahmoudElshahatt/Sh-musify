package com.shahtott.sh_musify.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MusicEntity::class], version = 3)
abstract class MusicDataBase : RoomDatabase() {
    abstract fun musicDao(): MusicDao
}