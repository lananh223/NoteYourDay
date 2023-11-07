package com.bignerdranch.android.noteyourday.MemoryList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.bignerdranch.android.noteyourday.Memory
import com.bignerdranch.android.noteyourday.MemoryRepository
import java.io.File
import java.util.UUID

class MemoryDetailViewModel:ViewModel() {

    private val memoryRepository = MemoryRepository.get()
    private val memoryIdLiveData = MutableLiveData<UUID>()

    val memoryLiveData: LiveData<Memory?> =
        Transformations.switchMap(memoryIdLiveData) { memoryId ->
            memoryRepository.getMemory(memoryId)
        }

    fun loadMemory(memoryId: UUID) {
        memoryIdLiveData.value = memoryId
    }

    // Save data when users edit, then tie in MemoryFragment lifecycle( onStop)
    fun saveMemory(memory: Memory) {
        memoryRepository.updateMemory(memory)
    }

    fun getPhotoFile(memory: Memory): File {
        return memoryRepository.getPhotoFile(memory)
    }
}