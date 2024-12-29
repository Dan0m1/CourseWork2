package com.example.coursework.data

import android.util.Log
import com.example.coursework.model.LoginModel
import com.example.coursework.network.CalendarAppApiService
import com.example.coursework.network.UserInfo

interface LoginRepository {
    suspend fun loginWithGoogle(idToken: String): LoginModel
}

class NetworkLoginRepository(
    val networkApiService: CalendarAppApiService
): LoginRepository {
    override suspend fun loginWithGoogle(idToken: String): LoginModel {
        Log.d("NetworkLoginRepository", "token: $idToken")
        val response = networkApiService.loginWithGoogle(UserInfo(idToken))
        Log.d("NetworkLoginRepository", response.toString())
        if (response.isEmpty()) {
            throw Exception("Invalid response")
        }
        return response[0]
    }
}