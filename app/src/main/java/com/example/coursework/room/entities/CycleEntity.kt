package com.example.coursework.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Cycle(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "global_id")
    val globalId: Int? = null,
    @ColumnInfo(name = "status")
    val status: CycleStatus = CycleStatus.ACTIVE,
    @ColumnInfo(name = "start_date")
    val startDate: String,
    @ColumnInfo(name = "end_date")
    val endDate: String,
    @ColumnInfo(name = "last_modified_at")
    val lastModifiedAt: String
)

enum class CycleStatus {
    ACTIVE,
    DELETED
}
