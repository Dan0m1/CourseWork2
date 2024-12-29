package com.example.coursework.room.repositories.day

import com.example.coursework.room.entities.Day
import com.example.coursework.room.entities.DayCategory
import kotlinx.coroutines.flow.Flow

interface DayRepository {
    suspend fun getModifiedDaysSince(lastSyncTime: String): List<Day>
    suspend fun upsertDay(day: Day)
    suspend fun upsertDays(days: List<Day>)
    suspend fun getAllDays(): List<Day>?
    suspend fun getDaysByCategory(category: DayCategory): List<Day>
    fun getDaysBetween(startDate: String, endDate: String): Flow<List<Day>>
    suspend fun updateCycleId(localId: Int, globalId: Int)
    suspend fun deleteAllDays()
    suspend fun deleteDays(days: List<Day>)
    suspend fun updateDaysCategory(newCategory: DayCategory)
}