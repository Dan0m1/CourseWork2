package com.example.coursework.room.repositories.cycle

import android.content.Context
import com.example.coursework.room.daos.CycleDao
import com.example.coursework.room.entities.Cycle
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CycleRepoImpl @Inject constructor(
    private val cycleDao: CycleDao,
    @ApplicationContext private val context: Context
) : CycleRepository{
    override suspend fun insertCycle(cycle: Cycle): Long {
        return cycleDao.insertCycle(cycle)
    }

    override suspend fun updateCycle(cycle: Cycle) {
        cycleDao.updateCycle(cycle)
    }

    override fun getCycleByDate(date: String): Flow<Cycle?> {
        return cycleDao.getCycleByDate(date)
    }

    override suspend fun getAllCycles(): List<Cycle> {
        return cycleDao.getAllCycles()
    }

    override suspend fun getCycleByGlobalId(globalId: Int): Cycle? {
        return cycleDao.getCycleByGlobalId(globalId)
    }

    override suspend fun updateCycleGlobalId(localId: Int, globalId: Int) {
        cycleDao.updateCycleGlobalId(localId, globalId)
    }

    override suspend fun deleteCycle(cycle: Cycle) {
        cycleDao.deleteCycle(cycle)
    }

    override suspend fun deleteAllCycles() {
        cycleDao.deleteAllCycles()
    }

    override suspend fun deactivateCycles(cycleId: List<Int>) {
        cycleDao.deactivateCycle(cycleId)
    }

    override suspend fun deactivateAllCycles() {
        cycleDao.deactivateAllCycles()
    }

    override suspend fun updateCyclesLastModifiedAt(cyclesToUpdate: List<Int>, lastModifiedAt: String) {
        cycleDao.updateCyclesLastModifiedAt(cyclesToUpdate, lastModifiedAt)
    }
}