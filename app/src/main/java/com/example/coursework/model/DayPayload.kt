package com.example.coursework.model

import com.example.coursework.room.entities.DayCategory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DayPayload(
    @SerialName(value = "cycle_id")
    val cycleId: Int, // Local Cycle ID
    val date: String,
    @SerialName(value = "day_category")
    val dayCategory: DayCategory,
    val hydration: Float?,
    @SerialName(value = "sleep_hours")
    val sleepHours: Float?,
    @SerialName(value = "last_modified_at")
    val lastModifiedAt: String,
    val notes: List<NotePayload>?
)