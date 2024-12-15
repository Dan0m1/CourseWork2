package com.example.coursework.network

import com.example.coursework.model.LoginModel
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface CalendarAppApiService {
    @POST("auth/google/login")
    suspend fun loginWithGoogle(@Body idToken: String): List<LoginModel>
}