package com.example.coursework.room.repositories.cycle

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.coursework.room.entities.Cycle
import com.example.coursework.room.entities.Day
import com.example.coursework.room.entities.DayCategory
import kotlinx.coroutines.flow.Flow

interface CycleRepository {
    suspend fun insertCycle(cycle: Cycle): Long
    suspend fun updateCycle(cycle: Cycle)
    fun getCycleByDate(date: String): Flow<Cycle?>
    suspend fun getAllCycles(): List<Cycle>
    suspend fun getCycleByGlobalId(globalId: Int): Cycle?
    suspend fun updateCycleGlobalId(localId: Int, globalId: Int)
    suspend fun deleteCycle(cycle: Cycle)
    suspend fun deleteAllCycles()
    suspend fun deactivateCycles(cycleId: List<Int>)
    suspend fun deactivateAllCycles()
    suspend fun updateCyclesLastModifiedAt(cyclesToUpdate: List<Int>, lastModifiedAt: String)
}