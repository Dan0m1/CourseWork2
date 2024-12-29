package com.example.coursework.ui.screens.calendar

import android.util.Log
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.coursework.ui.navigation.NavigationDestination
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object MenstrualCalendarDestination: NavigationDestination {
    override val route: String = "menstrual_calendar"
}

@Composable
fun CalendarScreen(
    viewModel: CalendarScreenViewModel,
    onCancel: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val today = LocalDate.now()
    val startYear = today.minusYears(2).withMonth(1).withDayOfMonth(1)
    val endYear = today.withMonth(12).withDayOfMonth(31)

    val months = remember { generateMonthsInRange(startYear, endYear) }

    val pagerState = rememberPagerState(initialPage = months.indexOfFirst { it.first.month == today.month && it.first.year == today.year }, pageCount = {months.size})

    Column(modifier = Modifier.fillMaxSize()) {
        Log.d("CalendarScreen", "Selected dates: ${uiState.selectedDates}")

        VerticalPager(
            state = pagerState,
            pageSize = PageSize.Fixed(270.dp),
            modifier = Modifier.weight(1f)
        ) { page ->
            MonthSection(
                month = months[page].first,
                days = months[page].second,
                selectedDates = uiState.selectedDates,
                onDateSelected = viewModel::onDateSelected
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onCancel) {
                Text(text = "Скасувати", color = MaterialTheme.colorScheme.error)
            }
            TextButton(onClick = {
                viewModel.onSave()
                onCancel()
            }) {
                Text(text = "Зберегти", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun MonthSection(
    month: LocalDate,
    days: List<LocalDate>,
    selectedDates: List<LocalDate>,
    onDateSelected: (LocalDate) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = month.format(DateTimeFormatter.ofPattern("LLLL yyyy")),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            textAlign = TextAlign.Center
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            items(days[0].dayOfWeek.value - 1) {
                Box(modifier = Modifier.size(40.dp))
            }
            items(days) { day ->
                DayItem(
                    date = day,
                    isSelected = selectedDates.contains(day),
                    onDateSelected = onDateSelected
                )
            }
        }
    }
}

@Composable
fun DayItem(date: LocalDate, isSelected: Boolean, onDateSelected: (LocalDate) -> Unit) {
    val backgroundColor = if (isSelected) Color(0xFFE57373) else Color.Transparent
    val textColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground

    Box(
        modifier = Modifier
            .size(40.dp)
            .padding(4.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable { onDateSelected(date) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            color = textColor,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

fun generateMonthsInRange(startDate: LocalDate, endDate: LocalDate): List<Pair<LocalDate, List<LocalDate>>> {
    val months = mutableListOf<Pair<LocalDate, List<LocalDate>>>()
    var currentMonth = startDate.withDayOfMonth(1)

    while (!currentMonth.isAfter(endDate)) {
        val daysInMonth = currentMonth.lengthOfMonth()
        val days = (1..daysInMonth).map { currentMonth.withDayOfMonth(it) }
        months.add(currentMonth to days)
        currentMonth = currentMonth.plusMonths(1)
    }

    return months
}
