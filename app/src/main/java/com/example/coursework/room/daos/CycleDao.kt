package com.example.coursework.room.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.coursework.room.entities.Cycle
import kotlinx.coroutines.flow.Flow

@Dao
interface CycleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCycle(cycle: Cycle): Long

    @Transaction
    @Query("SELECT * FROM Cycle WHERE id = :id LIMIT 1")
    suspend fun getCycleById(id: Int): Cycle?

    @Update
    suspend fun updateCycle(cycle: Cycle)

    @Query("SELECT * FROM Cycle WHERE start_date <= :date AND end_date >= :date AND status = 'ACTIVE' LIMIT 1")
    fun getCycleByDate(date: String): Flow<Cycle?>

    @Query("SELECT * FROM Cycle")
    suspend fun getAllCycles(): List<Cycle>

    @Query("SELECT * FROM Cycle WHERE global_id = :globalId LIMIT 1")
    suspend fun getCycleByGlobalId(globalId: Int): Cycle?

    @Query("UPDATE Cycle SET global_id = :globalId WHERE id = :localId")
    suspend fun updateCycleGlobalId(localId: Int, globalId: Int)

    @Query("UPDATE Cycle SET status = :status WHERE id = :localId")
    suspend fun updateCycleStatus(localId: Int, status: String)

    @Delete
    suspend fun deleteCycle(cycle: Cycle)

    @Transaction
    @Query("UPDATE Cycle SET status = 'DELETED' WHERE id IN (:cycleId)")
    suspend fun deactivateCycle(cycleId: List<Int>)

    @Transaction
    @Query("UPDATE Cycle SET status = 'DELETED'")
    suspend fun deactivateAllCycles()

    @Transaction
    @Query("DELETE FROM Cycle")
    suspend fun deleteAllCycles()

    @Transaction
    @Query("UPDATE Cycle SET last_modified_at = :lastModifiedAt WHERE id IN (:cyclesToUpdate)")
    suspend fun updateCyclesLastModifiedAt(cyclesToUpdate: List<Int>, lastModifiedAt: String)
}