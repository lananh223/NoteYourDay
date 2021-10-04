package com.bignerdranch.android.noteyourday.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bignerdranch.android.noteyourday.Memory

@Database(entities = [Memory::class], version = 1)
@TypeConverters(MemoryTypeConverters::class)
abstract class MemoryDatabase:RoomDatabase(){
    abstract fun memoryDao():MemoryDao
}