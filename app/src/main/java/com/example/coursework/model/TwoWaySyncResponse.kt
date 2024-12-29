package com.example.coursework.model

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.example.coursework.room.entities.CycleStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TwoWaySyncResponse(
    @SerialName(value = "updated_cycles")
    val updatedCycles: List<CycleResponse>,
)

@Serializable
data class CycleResponse(
    val id: Int,
    @SerialName(value = "global_id")
    val globalId: Int? = null,
    val status: CycleStatus = CycleStatus.ACTIVE,
    @SerialName(value = "start_date")
    val startDate: String,
    @SerialName(value = "end_date")
    val endDate: String,
    @SerialName(value = "last_modified_at")
    val lastModifiedAt: String
)
