package com.example.coursework.ui.screens.home

import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coursework.room.entities.Day
import com.example.coursework.room.entities.DayCategory
import com.example.coursework.ui.navigation.NavigationDestination
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

object HomeDestination : NavigationDestination {
    override val route = "home"
}

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onAddPeriodClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    Surface(Modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(48.dp)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF6F7FB))
            ) {
                CalendarHeader(
                    isLoading = uiState.isLoading,
                    selectedDayIndex = uiState.selectedDayIndex,
                    currentPage = uiState.currentPage,
                    weeks = uiState.weeks,
                    onDayClick = { viewModel.onDaySelected(it) },
                    onWeekSwipe = { viewModel.swipePage(it) }
                )
                DayInfo(
                    headlineMessage = uiState.weeks[uiState.currentPage].days[uiState.selectedDayIndex].headlineMessage,
                    bodyMessage = uiState.weeks[uiState.currentPage].days[uiState.selectedDayIndex].bodyMessage,
                    dayCategory = uiState.weeks[uiState.currentPage].days[uiState.selectedDayIndex].dayCategory,
                    onAddPeriodClick = onAddPeriodClick
                )

                if (!uiState.isCycleLoading) {
                    CycleHistorySection(
                        isLoading = uiState.isCycleLoading,
                        cycleInfo = uiState.cycleInfo!!
                    )
                }
                Log.d("HomeScreen", uiState.toString())
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(
                        onClick = viewModel::resetAll,
                        modifier = Modifier
                            .background(Color(0xFFE57373))
                            .clip(RoundedCornerShape(20.dp))
                    ) {
                        Text(text = "ВИДАЛИТИ ВСЕ", color = Color.White)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(
                        onClick = viewModel::sync,
                        modifier = Modifier
                            .background(Color(0xFFE57373))
                            .clip(RoundedCornerShape(20.dp))
                    ) {
                        Text(text = "ПРИМУСОВА СИНХРОНІЗАЦІЯ", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarHeader(
    isLoading: Boolean,
    currentPage: Int,
    selectedDayIndex: Int,
    weeks: List<StateWeek>,
    onDayClick: (Int) -> Unit,
    onWeekSwipe: (Int) -> Unit,
) {

    val selectedCircleOffsetX by animateDpAsState(
        targetValue
        = ((selectedDayIndex) * 52).dp, label = "selected_circle_animation"
    )

    if (isLoading) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(48.dp)
        )
    } else if (weeks.isNotEmpty() && weeks[currentPage].days.isNotEmpty()) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val dateParts: List<String> = weeks[currentPage].days[selectedDayIndex].date.split('-')
            Text(
                text = "${dateParts[2]} ${getMonthName(dateParts[1])}",
                fontSize = 20.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .width(360.dp)
                    .heightIn(min = 48.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Canvas(modifier = Modifier
                        .size(48.dp)
                        .offset(selectedCircleOffsetX)) {
                        drawCircle(
                            color = getDayCategoryColor(weeks[currentPage].days[selectedDayIndex].dayCategory),
                        )
                    }
                }
                WeekPager(
                    currentPage = currentPage,
                    weeks = weeks,
                    onWeekChange = onWeekSwipe,
                    onDayClick = onDayClick
                )
            }
        }
    }
}

@Composable
fun DayItem(
    index: Int,
    day: String,
    textColor: Color,
    containerColor: Color,
    borderColor: Color,
    fontWeight: FontWeight,
    onDayClick: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(containerColor)
            .border(2.dp, borderColor, CircleShape)
            .clickable { onDayClick(index) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day,
            color = textColor,
            fontSize = 16.sp,
            fontWeight = fontWeight
        )
    }
}

@Composable
fun WeekPager(
    weeks: List<StateWeek>,
    currentPage: Int,
    onWeekChange: (Int) -> Unit,
    onDayClick: (Int) -> Unit,
) {
    val pagerState = rememberPagerState(initialPage = 1, pageCount = { weeks.size })

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collect { page ->
            onWeekChange(page)
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collect { page ->
            if (currentPage == 1 && page == 0)
                pagerState.scrollToPage(1)
        }
    }

    HorizontalPager(
        state = pagerState,
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) { page ->
        WeekView(
            week = weeks[page],
            onDayClick = onDayClick
        )
    }
}

@Composable
fun WeekView(
    week: StateWeek,
    onDayClick: (Int) -> Unit
) {
    val currentDate = LocalDate.now().toString()
    Row(
        horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        week.days.forEachIndexed { i, day ->
            val isCurrentDate = currentDate == day.date
            DayItem(
                index = i,
                day = day.date.split('-')[2],
                textColor = getDayTextColor(day.dayCategory),
                containerColor = getDayContainerColor(day.dayCategory),
                borderColor = getDayBorderColor(day.dayCategory),
                fontWeight = if (isCurrentDate) FontWeight.Bold else FontWeight.Normal,
                onDayClick = onDayClick
            )
        }
    }
}

@Composable
fun DayInfo(
    headlineMessage: String,
    bodyMessage: String,
    dayCategory: DayCategory,
    onAddPeriodClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = headlineMessage,
            fontSize = 25.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = bodyMessage,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(40.dp))
        Button(
            onClick = { onAddPeriodClick() },
            colors = ButtonDefaults.buttonColors(
                containerColor = when (dayCategory) {
                    DayCategory.CONFIRMED_PERIOD,
                    DayCategory.ORDINARY,
                    DayCategory.NO_INFO -> Color(0xFFE57373)

                    DayCategory.OVULATION,
                    DayCategory.FERTILE -> Color(0xFF459093)

                    DayCategory.DELAYED -> Color(0xFFE8EAF6)
                }
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                text = when (dayCategory) {
                    DayCategory.CONFIRMED_PERIOD -> "Змінити дати місячних"

                    else -> "Позначити дні місячних"
                },
                color = when (dayCategory) {
                    DayCategory.DELAYED -> Color.Black
                    else -> Color.White
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
                )
        }
    }
}

@Composable
fun DailyNotesSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        // Add Symptoms Card
        Box(
            modifier = Modifier
                .size(120.dp, 120.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .clickable { /* Handle click */ }
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Додати примітки",
                    fontSize = 14.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE57373)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Advice Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(12.dp)
        ) {
            Column {
                Text(
                    text = "Сьогодні немає сторіз",
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Але у нас є багато надійного контенту для вас!",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Натисніть, щоб дізнатися",
                    fontSize = 12.sp,
                    color = Color(0xFFE57373),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CycleHistorySection(
    isLoading: Boolean,
    cycleInfo: StateCycleInfo
) {
    if(!isLoading) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(12.dp)
        ) {
            Text(
                text = "Мої цикли",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))

            Spacer(modifier = Modifier.height(12.dp))

            val cycleStartDate = cycleInfo.startDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            val cycleCurrentLength = Period.between(cycleInfo.startDate, LocalDate.now()).days
            Text(
                text = "Поточний цикл: $cycleCurrentLength днів",
                fontSize = 16.sp,
                color = Color.Black,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Початок: $cycleStartDate",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun HomeScreenPreview(viewModel: HomeViewModel = viewModel()) {
    HomeScreen(viewModel, {})
}

fun getMonthName(month: String) = when (month) {
    "01" -> "січня"
    "02" -> "лютого"
    "03" -> "березня"
    "04" -> "квітня"
    "05" -> "травня"
    "06" -> "червня"
    "07" -> "липня"
    "08" -> "серпня"
    "09" -> "вересня"
    "10" -> "жовтня"
    "11" -> "листопада"
    "12" -> "грудня"
    else -> ""
}

fun getDayCategoryColor(category: DayCategory) = when (category) {
    DayCategory.ORDINARY,
    DayCategory.NO_INFO -> Color(0xC4D9D6D6)

    else -> Color(0xFFFFFFFF)
}

fun getDayTextColor(category: DayCategory) = when (category) {
    DayCategory.CONFIRMED_PERIOD -> Color.White
    DayCategory.FERTILE -> Color(0xFF459093)
    DayCategory.OVULATION -> Color(0xE96BC9FA)
    DayCategory.DELAYED -> Color.White
    else -> Color.Black
}

fun getDayContainerColor(category: DayCategory) = when (category) {
    DayCategory.CONFIRMED_PERIOD -> Color(0xFFE57373)
    DayCategory.DELAYED -> Color(0xFF363B3B)
    else -> Color.Transparent
}

fun getDayBorderColor(category: DayCategory) = when (category) {
    DayCategory.OVULATION -> Color(0xE99AD2EF)
    else -> Color.Transparent
}
