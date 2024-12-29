package com.example.coursework.room.daos


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.coursework.room.entities.Day
import com.example.coursework.room.entities.DayCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface DayDao {

    @Transaction
    @Query("SELECT * FROM days WHERE last_modified_at > :lastSyncTime")
    suspend fun getModifiedDaysSince(lastSyncTime: String): List<Day>

    @Transaction
    @Query("SELECT * FROM days")
    suspend fun getAllDays(): List<Day>?

    @Transaction
    @Query("SELECT * FROM days WHERE day_category = :category")
    suspend fun getDaysByCategory(category: DayCategory): List<Day>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDay(day: Day)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDays(days: List<Day>)

    @Transaction
    @Query("SELECT * FROM days WHERE date BETWEEN :startDate AND :endDate")
    fun getDaysBetween(startDate: String, endDate: String): Flow<List<Day>>

    @Transaction
    @Query("UPDATE days SET cycle_id = :globalId WHERE cycle_id = :localId")
    suspend fun updateCycleId(localId: Int, globalId: Int)

    @Transaction
    @Query("DELETE FROM days")
    suspend fun deleteAllDays()

    @Transaction
    @Query("UPDATE days SET day_category = :newCategory")
    suspend fun updateDaysCategoryWhereNot(newCategory: DayCategory)

    @Delete
    suspend fun deleteDays(days: List<Day>)

}