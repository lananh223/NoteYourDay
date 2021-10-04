package com.bignerdranch.android.noteyourday.MemoryList

import androidx.lifecycle.ViewModel
import com.bignerdranch.android.noteyourday.Memory
import com.bignerdranch.android.noteyourday.MemoryRepository
import java.util.*

class MemoryListViewModel : ViewModel() {

    private val memoryRepository = MemoryRepository.get()
    val memoryListLiveData = memoryRepository.getMemories()

    fun addMemory(memory: Memory){
        memoryRepository.addMemory(memory)
    }
    fun deleteMemory(memory: Memory){
        memoryRepository.deleteMemory(memory)
    }
}
