package com.example.coursework.ui.screens.calendar

import java.time.LocalDate

data class CalendarScreenUiState(
    val selectedDates: List<LocalDate> = emptyList(),
) {
    companion object {
        val default = CalendarScreenUiState()
    }
}