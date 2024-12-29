package com.example.coursework.room.repositories.sync

import android.content.Context
import com.example.coursework.EncryptedPrefsUtils
import com.example.coursework.model.CycleResponse
import com.example.coursework.model.CycleWithDaysPayload
import com.example.coursework.model.DayPayload
import com.example.coursework.model.NotePayload
import com.example.coursework.model.TwoWaySyncRequest
import com.example.coursework.model.TwoWaySyncResponse
import com.example.coursework.network.CalendarAppApiService
import com.example.coursework.room.daos.CycleDao
import com.example.coursework.room.daos.DayDao
import com.example.coursework.room.entities.Cycle
import com.example.coursework.room.entities.CycleStatus
import com.example.coursework.room.entities.Day
import com.example.coursework.room.entities.DayNote
import com.example.coursework.room.entities.NoteCategory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.util.Date
import javax.inject.Inject

class SyncRepoImpl @Inject constructor(
    private val dayDao: DayDao,
    private val cycleDao: CycleDao,
    private val backendApi: CalendarAppApiService,
    @ApplicationContext private val context: Context
) : SyncRepository {
    override suspend fun twoWaySync() {
        val lastSyncTime = EncryptedPrefsUtils.getLastSyncTime(context.applicationContext) ?: "2000-01-01T00:00:00Z"

        // Отримання змінених днів
        val modifiedDays = dayDao.getModifiedDaysSince(lastSyncTime)
        val localCycles = cycleDao.getAllCycles()

        // Перетворення локальних циклів у формат для синхронізації
        val cyclesWithDaysPayload: List<CycleWithDaysPayload> = withContext(Dispatchers.Default) {
            localCycles.map { cycle ->
                val days = modifiedDays.filter { it.cycleId == cycle.id }
                CycleWithDaysPayload(
                    id = cycle.id,
                    globalId = cycle.globalId,
                    status = cycle.status,
                    startDate = cycle.startDate,
                    endDate = cycle.endDate,
                    lastModifiedAt = cycle.lastModifiedAt,
                    days = days.map { day ->
                        DayPayload(
                            cycleId = day.cycleId,
                            date = day.date,
                            dayCategory = day.dayCategory,
                            hydration = day.hydration,
                            sleepHours = day.sleepHours,
                            lastModifiedAt = day.lastModifiedAt,
                            notes = day.dailyNotes.map { note ->
                                NotePayload(name = note.name, category = note.category.name)
                            }
                        )
                    },

                    )
            }
        }

        // Виклик API для синхронізації
        val response: TwoWaySyncResponse = withContext(Dispatchers.IO) {
            backendApi.sync(
                bearer = "Bearer ${EncryptedPrefsUtils.getAccessToken(context.applicationContext)}",
                twoWaySyncRequest = TwoWaySyncRequest(
                    lastSyncTime = lastSyncTime,
                    localCycles = cyclesWithDaysPayload
                )
            )
        }

        // Оновлення локальних даних на основі відповіді
        withContext(Dispatchers.IO) {
            response.updatedCycles.forEach { cycleResponse ->
                // Отримуємо локальний цикл за `id`
                val localCycle = cycleDao.getCycleById(cycleResponse.id)

                if (localCycle != null) {
                    // Оновлення існуючого циклу
                    val updatedCycle = localCycle.copy(
                        globalId = cycleResponse.globalId ?: localCycle.globalId,
                        startDate = cycleResponse.startDate,
                        endDate = cycleResponse.endDate,
                        status = cycleResponse.status,
                        lastModifiedAt = cycleResponse.lastModifiedAt
                    )
                    cycleDao.updateCycle(updatedCycle)
                } else {
                    // Додавання нового циклу
                    val newCycle = Cycle(
                        id = cycleResponse.id,
                        globalId = cycleResponse.globalId,
                        startDate = cycleResponse.startDate,
                        endDate = cycleResponse.endDate,
                        status = cycleResponse.status,
                        lastModifiedAt = cycleResponse.lastModifiedAt
                    )
                    cycleDao.insertCycle(newCycle)
                }

            }
        }

        // Оновлення часу останньої синхронізації
        EncryptedPrefsUtils.saveLastSyncTime(context.applicationContext, LocalDateTime.now().toString())
    }


}