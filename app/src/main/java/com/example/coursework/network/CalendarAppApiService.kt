package com.example.coursework.network

import com.example.coursework.model.Cycle
import com.example.coursework.model.LoginModel
import com.example.coursework.model.TwoWaySyncRequest
import com.example.coursework.model.TwoWaySyncResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

@Serializable
data class UserInfo(
    @SerialName("idToken") val token: String
)

interface CalendarAppApiService {
    @POST("auth/google/login")
    suspend fun loginWithGoogle(@Body userData: UserInfo): List<LoginModel>

    @POST("sync/days")
    suspend fun sync(@Header("Authorization") bearer: String, @Body twoWaySyncRequest: TwoWaySyncRequest): TwoWaySyncResponse
}