package com.bignerdranch.android.noteyourday.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bignerdranch.android.noteyourday.Memory.Memory
import java.util.*

@Dao
interface MemoryDao {

    @Query("SELECT * FROM memory")
    fun getMemories(): LiveData<List<Memory>>

    @Query("SELECT * FROM memory WHERE id=(:id)")
    fun getMemory(id: UUID): LiveData<Memory?>

    @Update
    fun updateMemory(memory: Memory)

    @Insert
    fun addMemory(memory: Memory)

    @Delete
    fun deleteMemory(memory: Memory)
}