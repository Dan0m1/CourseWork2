package com.example.coursework.ui.screens.calendar

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coursework.EncryptedPrefsUtils
import com.example.coursework.room.PredictCycleUtils
import com.example.coursework.room.entities.DayCategory
import com.example.coursework.room.repositories.cycle.CycleRepository
import com.example.coursework.room.repositories.day.DayRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalendarScreenViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    val dayRepository: DayRepository,
    val cycleRepository: CycleRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CalendarScreenUiState.default)
    val uiState: StateFlow<CalendarScreenUiState> = _uiState

    init {
        viewModelScope.launch {
            val periodsFromDatabase = dayRepository.getDaysByCategory(DayCategory.CONFIRMED_PERIOD)
            val selectedDates = periodsFromDatabase.map { LocalDate.parse(it.date) }.sorted()
            _uiState.update {
                _uiState.value.copy(
                    selectedDates = selectedDates,
                )
            }
            Log.d("CalendarScreenViewModel", "init: $selectedDates")
        }
    }

    fun onDateSelected(date: LocalDate) {
        val selectedDates = _uiState.value.selectedDates.toMutableList()

        val periodLength = EncryptedPrefsUtils.getPeriodLength(context.applicationContext)

        if(selectedDates.isEmpty()) {
            _uiState.update {
                EncryptedPrefsUtils.savePeriodLength(context.applicationContext, 5)

                _uiState.value.copy(
                    selectedDates = getPredictedDates(date, periodLength),
                )
            }
            return
        }

        if (selectedDates.contains(date)) {
            selectedDates.remove(date)
        } else {
            selectedDates.addAll(getPredictedDates(date, periodLength))
        }

        _uiState.update {
            _uiState.value.copy(
                selectedDates = selectedDates.sorted(),
            )
        }

    }

    fun onSave() {
        viewModelScope.launch {
            val selectedDates = uiState.value.selectedDates
            val selectedPeriods = getDateRanges(selectedDates)
            PredictCycleUtils.calculateAndSaveCycles(
                selectedPeriods = selectedPeriods,
                lutealPhase = EncryptedPrefsUtils.getLutealPhaseLength(context.applicationContext),
                dayRepository = dayRepository,
                cycleRepository = cycleRepository,
                context = context.applicationContext
            )
        }
    }

    private fun getPredictedDates(startDate: LocalDate, periodLength: Int): List<LocalDate> {
        return (1..periodLength).map { startDate.plusDays(it.toLong()-1) }
    }

    private fun getDateRanges(dates: List<LocalDate>): List<Pair<LocalDate, LocalDate>> {
        if (dates.isEmpty()) return emptyList()

        val sortedDates = dates.sorted()
        val ranges = mutableListOf<Pair<LocalDate, LocalDate>>()
        var rangeStart = sortedDates.first()

        for (i in 1 until sortedDates.size) {
            val current = sortedDates[i]
            val previous = sortedDates[i - 1]

            if (current != previous.plusDays(1)) {
                ranges.add(Pair(rangeStart, previous))
                rangeStart = current
            }
        }

        ranges.add(Pair(rangeStart, sortedDates.last()))

        return ranges
    }
}