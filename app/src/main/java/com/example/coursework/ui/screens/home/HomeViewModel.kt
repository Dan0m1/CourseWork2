package com.example.coursework.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coursework.room.entities.Day
import com.example.coursework.room.entities.DayCategory
import com.example.coursework.room.repositories.cycle.CycleRepository
import com.example.coursework.room.repositories.day.DayRepository
import com.example.coursework.room.repositories.sync.SyncRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val dayRepository: DayRepository,
    val cycleRepository: CycleRepository,
    val syncRepository: SyncRepository
): ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.default)
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        initHomeScreenData()
    }

    fun initHomeScreenData() {
        val today = LocalDate.now()
        val todayDayOfWeek = today.dayOfWeek.value
        val firstDayOfWeek = today.minusDays(todayDayOfWeek.toLong()-1)
        val startDate = firstDayOfWeek.minusWeeks(1)
        val endDate = firstDayOfWeek.plusDays(13)

        viewModelScope.launch(Dispatchers.IO) {
            dayRepository.getDaysBetween(startDate.toString(), endDate.toString())
                .collect { retrievedDays ->
                    _uiState.update { it.copy(isLoading = true) }
                    val filledDays = fillMissingDays(startDate, endDate, retrievedDays)
                    Log.d("HomeScreen - initHomeScreenData", retrievedDays.toString())
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            selectedDayIndex = todayDayOfWeek - 1,
                            weeks = filledDays.chunked(7).map { days ->
                                StateWeek(days = days)
                            }
                        )
                    }
                }
        }

        viewModelScope.launch {
            cycleRepository.getCycleByDate(today.toString()).collect { cycle ->
                if (cycle != null) {
                    _uiState.update {
                        it.copy(isCycleLoading = true)
                    }
                    _uiState.update {
                        it.copy(
                            cycleInfo = StateCycleInfo(startDate = LocalDate.parse(cycle.startDate)),
                            isCycleLoading = false,
                        )
                    }
                }
                else {
                    _uiState.update {
                        _uiState.value.copy(isCycleLoading = true)
                    }
                }
            }
        }
    }

    fun swipePage(currentPage: Int) {
        if (currentPage == 0) {
            updateDataAfterSwipe(false, currentPage)
        } else if (currentPage == _uiState.value.weeks.size - 1) {
            updateDataAfterSwipe(true, currentPage)
        } else {
            _uiState.update {
                _uiState.value.copy(currentPage = currentPage)
            }
        }
    }


    fun updateDataAfterSwipe(isSwipeLeft: Boolean, currentPage: Int) {
        val firstDayOfWeek = LocalDate.parse(_uiState.value.weeks.first().days.first().date)
        val lastDayOfWeek = LocalDate.parse(_uiState.value.weeks.last().days.last().date)

        val startDate = if (isSwipeLeft) lastDayOfWeek.plusDays(1) else firstDayOfWeek.minusWeeks(1)
        val endDate = if (isSwipeLeft) lastDayOfWeek.plusWeeks(1) else firstDayOfWeek.minusDays(1)

        viewModelScope.launch(Dispatchers.IO) {
            dayRepository.getDaysBetween(
                startDate = startDate.toString(),
                endDate = endDate.toString()
            ).collect { retrievedDays ->
                val filledDays = fillMissingDays(startDate, endDate, retrievedDays)

                _uiState.update {
                    Log.d("HomeScreen - updateDataAfterSwipe", _uiState.value.currentPage.toString())
                    _uiState.value.copy(
                        currentPage = if (currentPage == 0) 1 else currentPage,
                        weeks = if (isSwipeLeft) {
                            _uiState.value.weeks + listOf(StateWeek(days = filledDays))
                        } else {
                            listOf(StateWeek(days = filledDays)) + _uiState.value.weeks
                        }
                    )
                }

            }
            Log.d("HomeScreen - updateDataAfterSwipe", _uiState.value.currentPage.toString())
        }
    }


    fun onDaySelected(index: Int) {
        _uiState.update {
            _uiState.value.copy(selectedDayIndex = index)
        }
    }

    fun resetAll() {
        viewModelScope.launch {
            dayRepository.deleteAllDays()
            cycleRepository.deactivateAllCycles()
        }
    }

    fun sync() {
        viewModelScope.launch {
            try {
                syncRepository.twoWaySync()
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error syncing", e)
            }
        }
    }

}


private fun fillMissingDays(
    startDate: LocalDate,
    endDate: LocalDate,
    existingDays: List<Day>
): List<StateDay> {
     val result = (startDate..endDate).map { date ->
        val existingDay = existingDays.find { LocalDate.parse(it.date) == date }
        val dayCategory = existingDay?.dayCategory ?: DayCategory.NO_INFO
        Log.d("HomeScreen - fillMissingDays", dayCategory.toString())

        val headlineMessage = when (dayCategory) {
            DayCategory.CONFIRMED_PERIOD -> "Місячні"
            DayCategory.FERTILE -> "Фертильний день"
            DayCategory.OVULATION -> "День овуляції"
            DayCategory.ORDINARY -> {
                when {
                    existingDay?.daysToNextCycle != null -> "До наступних місячних: ${existingDay.daysToNextCycle} днів"
                    existingDay?.daysToOvulation != null -> "До овуляції: ${existingDay.daysToOvulation} днів"
                    else -> "Немає інформації"
                }
            }
            else -> "Немає інформації"
        }
        Log.d("HomeScreen - fillMissingDays", headlineMessage)

        val bodyMessage = when (dayCategory) {
            DayCategory.CONFIRMED_PERIOD -> {
                if (existingDay?.isStartOfPeriod == true) "Перший день місячних"
                else if (existingDay?.isEndOfPeriod == true) "Останній день місячних"
                else "Місячні тривають"
            }
            DayCategory.FERTILE -> {
                "Днів до овуляції: ${existingDay?.daysToOvulation ?: "?"}"
            }
            DayCategory.OVULATION -> "Високий шанс завагітніти"
            DayCategory.ORDINARY -> {
                when {
                    existingDay?.daysToNextCycle != null -> "Днів до наступних місячних: ${existingDay.daysToNextCycle}"
                    existingDay?.daysToOvulation != null -> "Днів до овуляції: ${existingDay.daysToOvulation}"
                    else -> "Низький шанс завагітніти"
                }
            }
            else -> "Позначте дні на календарі"
        }
        Log.d("HomeScreen - fillMissingDays", bodyMessage)

        StateDay(
            date = date.toString(),
            dayCategory = dayCategory,
            hydration = existingDay?.hydration ?: 0.0f,
            sleepHours = existingDay?.sleepHours ?: 0.0f,
            dailyNotes = existingDay?.dailyNotes ?: emptyList(),
            headlineMessage = headlineMessage,
            bodyMessage = bodyMessage
        )
    }
    Log.d("HomeScreen - fillMissingDays", result.toString())
    return result
}



private operator fun LocalDate.rangeTo(other: LocalDate): List<LocalDate> {
    return generateSequence(this) { if (it < other) it.plusDays(1) else null }.toList()
}