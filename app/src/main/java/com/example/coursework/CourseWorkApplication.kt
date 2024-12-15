package com.example.coursework

import android.app.Application
import com.example.coursework.data.AppContainer
import com.example.coursework.data.DefaultAppContainer

class CourseWorkApplication: Application() {
    lateinit var appContainer: AppContainer
    override fun onCreate() {
        super.onCreate()
        appContainer = DefaultAppContainer()
    }
}