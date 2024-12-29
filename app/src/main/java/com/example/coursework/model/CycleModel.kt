package com.example.coursework.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Cycle(
    val id: Int,
    @SerialName(value = "start_date")
    val startDate: String,
    @SerialName(value = "end_date")
    val endDate: String,
    @SerialName(value = "period_end_date")
    val periodEndDate: String,
    @SerialName(value = "fertility_window")
    val fertilityWindow: FertilityWindow?,
    @SerialName(value = "daily_logs")
    val dailyLogs: List<DailyLog>,
    @SerialName(value = "last_modified_at")
    val lastModifiedAt: String
)

@Serializable
data class FertilityWindow(
    @SerialName(value = "cycle_id")
    val cycleId: Int,
    @SerialName(value = "start_date")
    val startDate: String,
    @SerialName(value = "end_date")
    val endDate: String,
    @SerialName(value = "ovulation_date")
    val ovulationDate: String,
    @SerialName(value = "last_modified_at")
    val lastModifiedAt: String
)

@Serializable
data class DailyLog(
    @SerialName(value = "cycle_id")
    val cycleId: Int,
    val date: String,
    @SerialName(value = "sleep_hours")
    val sleepHours: Float,
    val hydration: Float,
    @SerialName(value = "daily_notes")
    val dailyNotes: List<DailyNote>,
    @SerialName(value = "last_modified_at")
    val lastModifiedAt: String
)

@Serializable
data class DailyNote(
    val category: String,
    val name: String
)