package com.example.coursework.ui.screens.home

import androidx.compose.foundation.pager.PagerState
import com.example.coursework.room.entities.DayCategory
import com.example.coursework.room.entities.DayNote
import java.time.LocalDate
import java.util.Date

data class HomeUiState(
    val isInit: Boolean = true,
    val isLoading: Boolean = true,
    val isCycleLoading: Boolean = true,
    val currentPage: Int = 1,
    val selectedDayIndex: Int = 0,
    val weeks: List<StateWeek> = listOf(),
    val cycleInfo: StateCycleInfo? = null,
) {
    companion object {
        val default: HomeUiState = HomeUiState()
    }
}

data class StateDay(
    val date: String,
    val headlineMessage: String,
    val bodyMessage: String,
    val dayCategory: DayCategory,
    val hydration: Float? = null,
    val sleepHours: Float? = null,
    val dailyNotes: List<DayNote> = listOf(),
)

data class StateCycleInfo(
    val startDate: LocalDate,
)

data class StateWeek(
    val days: List<StateDay>
)