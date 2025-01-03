package com.example.coursework

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration


import com.example.coursework.data.AppContainer
import com.example.coursework.data.DefaultAppContainer
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class CourseWorkApplication: Application(), Configuration.Provider {
    @Inject lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
    }


    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}