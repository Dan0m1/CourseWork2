package com.example.coursework.model

import com.example.coursework.room.entities.CycleStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TwoWaySyncRequest(
    @SerialName(value = "last_sync_time")
    val lastSyncTime: String,
    @SerialName(value = "local_cycles")
    val localCycles: List<CycleWithDaysPayload> = emptyList(),
)

@Serializable
data class CycleWithDaysPayload(
    val id: Int,
    @SerialName(value = "global_id")
    val globalId: Int?,
    @SerialName(value = "status")
    val status: CycleStatus,
    @SerialName(value = "start_date")
    val startDate: String,
    @SerialName(value = "end_date")
    val endDate: String,
    @SerialName(value = "last_modified_at")
    val lastModifiedAt: String,
    val days: List<DayPayload>
)