package com.bignerdranch.android.noteyourday.Memory

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.bignerdranch.android.noteyourday.database.MemoryDatabase
import java.io.File
import java.util.UUID
import java.util.concurrent.Executors

private const val DATABASE_NAME = "memory-database"

class MemoryRepository private constructor(context: Context) {

    //to store references to database & DAO objects
    private val database: MemoryDatabase = Room.databaseBuilder(
        context.applicationContext,
        MemoryDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val memoryDao = database.memoryDao()

    //Return executor instant that points to a new thread ( so no main thread)
    private val executor = Executors.newSingleThreadExecutor()
    private val filesDir = context.applicationContext.filesDir

    fun getMemories(): LiveData<List<Memory>> = memoryDao.getMemories()
    fun getMemory(id: UUID): LiveData<Memory?> = memoryDao.getMemory(id)
    fun updateMemory(memory: Memory) {
        executor.execute {
            memoryDao.updateMemory(memory)
        }
    }

    fun addMemory(memory: Memory) {
        executor.execute {
            memoryDao.addMemory(memory)
        }
    }

    fun deleteMemory(memory: Memory) {
        executor.execute {
            memoryDao.deleteMemory(memory)
        }
    }

    // Give photo a file location
    fun getPhotoFile(memory: Memory): File = File(filesDir, memory.photoFileName)

    // make Repository singleton
    companion object {
        private var INSTANCE: MemoryRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = MemoryRepository(context)
            }
        }

        fun get(): MemoryRepository {
            return INSTANCE ?: throw IllegalStateException("MemoryRepository must be initialized")
        }
    }
}