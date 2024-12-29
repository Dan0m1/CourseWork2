package com.example.coursework.room.repositories.day

import android.content.Context
import com.example.coursework.EncryptedPrefsUtils
import com.example.coursework.model.DayPayload
import com.example.coursework.model.NotePayload
import com.example.coursework.model.TwoWaySyncRequest
import com.example.coursework.model.TwoWaySyncResponse
import com.example.coursework.network.CalendarAppApiService
import com.example.coursework.room.daos.DayDao
import com.example.coursework.room.entities.Cycle
import com.example.coursework.room.entities.CycleStatus
import com.example.coursework.room.entities.Day
import com.example.coursework.room.entities.DayCategory
import com.example.coursework.room.entities.DayNote
import com.example.coursework.room.entities.NoteCategory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

class DayRepoImpl @Inject constructor(
    private val dayDao: DayDao,
    private val backendApi: CalendarAppApiService,
    @ApplicationContext private val context: Context
) : DayRepository {
    override suspend fun getModifiedDaysSince(lastSyncTime: String) = dayDao.getModifiedDaysSince(lastSyncTime)

    override suspend fun upsertDay(day: Day) = dayDao.upsertDay(day)

    override suspend fun upsertDays(days: List<Day>) = dayDao.upsertDays(days)

    override fun getDaysBetween(startDate: String, endDate: String) = dayDao.getDaysBetween(startDate, endDate)

    override suspend fun updateCycleId(localId: Int, globalId: Int) = dayDao.updateCycleId(localId, globalId)

    override suspend fun getAllDays(): List<Day>? = dayDao.getAllDays()

    override suspend fun getDaysByCategory(category: DayCategory) = dayDao.getDaysByCategory(category)

    override suspend fun deleteAllDays() = dayDao.deleteAllDays()

    override suspend fun deleteDays(days: List<Day>) = dayDao.deleteDays(days)

    override suspend fun updateDaysCategory(newCategory: DayCategory) = dayDao.updateDaysCategoryWhereNot(newCategory)


    private fun getCurrentTimestamp(): String {
        return Date().toInstant().toString()
    }
}