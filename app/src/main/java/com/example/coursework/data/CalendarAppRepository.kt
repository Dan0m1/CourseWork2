package com.example.coursework.data

import com.example.coursework.model.LoginModel
import com.example.coursework.network.CalendarAppApiService

interface LoginRepository {
    suspend fun loginWithGoogle(idToken: String): LoginModel
}

class NetworkLoginRepository(
    val networkApiService: CalendarAppApiService
): LoginRepository {
    override suspend fun loginWithGoogle(idToken: String): LoginModel {
        val response = networkApiService.loginWithGoogle(idToken)
        if (response.isEmpty()) {
            throw Exception("Invalid response")
        }
        return response[0]
    }
}