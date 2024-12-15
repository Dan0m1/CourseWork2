package com.example.coursework.data

import com.example.coursework.network.NetworkModule

interface AppContainer {
    val loginRepository: LoginRepository
}

class DefaultAppContainer: AppContainer {
    private val apiService = NetworkModule.apiService

    override val loginRepository: LoginRepository by lazy {
        NetworkLoginRepository(apiService)
    }
}