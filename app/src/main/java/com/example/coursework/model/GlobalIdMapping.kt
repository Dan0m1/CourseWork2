package com.example.coursework.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GlobalIdMapping(
    @SerialName(value = "local_id")
    val localId: Int,
    @SerialName(value = "global_id")
    val globalId: Int
)
