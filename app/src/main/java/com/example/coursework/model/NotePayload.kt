package com.example.coursework.model

import kotlinx.serialization.Serializable

@Serializable
data class NotePayload(
    val category: String,
    val name: String
)