package com.bignerdranch.android.noteyourday.Memory

import android.app.Application

// then add in Manifest, to initialize Repository when the app is launched
class MemoryIntentApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        MemoryRepository.initialize(this)
    }
}