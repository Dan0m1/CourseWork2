package com.example.coursework.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "days", indices = [Index(value = ["date"], unique = true)])
data class Day(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "cycle_id")
    var cycleId: Int,
    @ColumnInfo(name = "date")
    val date: String,
    @ColumnInfo(name = "day_category")
    val dayCategory: DayCategory,
    @ColumnInfo(name = "hydration")
    val hydration: Float? = null,
    @ColumnInfo(name = "sleep_hours")
    val sleepHours: Float? = null,
    @ColumnInfo(name = "daily_notes")
    val dailyNotes: List<DayNote> = listOf(),
    @ColumnInfo(name = "last_modified_at")
    val lastModifiedAt: String,

    @ColumnInfo(name = "is_start_of_period")
    val isStartOfPeriod: Boolean = false,
    @ColumnInfo(name = "is_end_of_period")
    val isEndOfPeriod: Boolean = false,
    @ColumnInfo(name = "days_to_next_cycle")
    val daysToNextCycle: Int? = null,
    @ColumnInfo(name = "days_to_ovulation")
    val daysToOvulation: Int? = null,
    @ColumnInfo(name = "days_from_ovulation")
    val daysFromOvulation: Int? = null
)

data class DayNote(
    val category: NoteCategory,
    val name: String
)

enum class NoteCategory {
    SYMPTOM,
    MOOD,
    SEXUAL_DESIRE,
    DIGESTION
}

enum class DayCategory {
    NO_INFO,
    CONFIRMED_PERIOD,
    ORDINARY,
    FERTILE,
    OVULATION,
    DELAYED
}