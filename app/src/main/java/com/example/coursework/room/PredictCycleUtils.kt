package com.example.coursework.room

import android.content.Context
import android.util.Log
import com.example.coursework.EncryptedPrefsUtils
import com.example.coursework.room.entities.Cycle
import com.example.coursework.room.entities.CycleStatus
import com.example.coursework.room.entities.Day
import com.example.coursework.room.entities.DayCategory
import com.example.coursework.room.repositories.cycle.CycleRepository
import com.example.coursework.room.repositories.day.DayRepository
import java.time.LocalDate
import java.time.LocalDateTime

object PredictCycleUtils {
    suspend fun calculateAndSaveCycles(
        context: Context,
        selectedPeriods: List<Pair<LocalDate, LocalDate>>,
        lutealPhase: Int,
        dayRepository: DayRepository,
        cycleRepository: CycleRepository
    ) {
        if (selectedPeriods.isEmpty()) {
            dayRepository.updateDaysCategory(DayCategory.NO_INFO)
            cycleRepository.deactivateAllCycles()
            return
        }

        val sortedPeriods = selectedPeriods.sortedBy { it.first }
        val averageCycleLength = calculateAverageCycleLength(sortedPeriods, context)
        val averagePeriodLength = calculateAveragePeriodLength(sortedPeriods, context)

        val existingDays = dayRepository.getAllDays() ?: emptyList()
        val existingCycles = cycleRepository.getAllCycles()
        val predictedCycles = mutableListOf<Day>()

        var previousCycleEnd: LocalDate? = null

        sortedPeriods.forEachIndexed { index, period ->
            val periodStart = period.first
            val periodEnd = period.second

            val relevantDays = existingDays.filter {
                val date = LocalDate.parse(it.date)
                date in periodStart..periodEnd
            }

            val cycleStart = previousCycleEnd?.plusDays(1) ?: periodStart
            val cycleEnd =
                if (index + 1 < sortedPeriods.size) sortedPeriods[index + 1].first.minusDays(1) else null
                    ?: periodEnd.plusDays(averageCycleLength.toLong() - (periodEnd.toEpochDay() - periodStart.toEpochDay() + 1))
            Log.d("AverageCycleLength", averageCycleLength.toString())

            val cycleId = findOrMergeCycle(
                cycleRepository,
                existingCycles,
                cycleStart,
                cycleEnd
            )

            predictedCycles.addAll(
                generateCycleDays(
                    cycleStart = cycleStart,
                    cycleEnd = cycleEnd,
                    periodStart = periodStart,
                    periodEnd = periodEnd,
                    ovulationDay = cycleEnd.minusDays(lutealPhase.toLong()),
                    existingDays = relevantDays,
                    cycleId = cycleId
                )
            )

            previousCycleEnd = cycleEnd
        }

        val lastCycleEnd = previousCycleEnd ?: sortedPeriods.last().second
        val currentDate = LocalDate.now()
        var nextCycleStart = lastCycleEnd.plusDays(1)
        val cyclesToUpdate = mutableListOf<Int>()

        while (nextCycleStart.year == currentDate.year) {
            val nextCycleEnd = nextCycleStart.plusDays(averageCycleLength.toLong() - 1)
            val ovulationDay = nextCycleEnd.minusDays(lutealPhase.toLong())

            val cycleId = findOrCreateCycle(
                cycleRepository,
                existingCycles,
                nextCycleStart,
                nextCycleEnd,
                existingDays
            )

            predictedCycles.addAll(
                generateCycleDays(
                    cycleStart = nextCycleStart,
                    cycleEnd = nextCycleEnd,
                    periodStart = nextCycleStart,
                    periodEnd = nextCycleStart.plusDays(averagePeriodLength.toLong() - 1),
                    ovulationDay = ovulationDay,
                    existingDays = existingDays,
                    cycleId = cycleId
                )
            )
            cyclesToUpdate.add(cycleId)

            nextCycleStart = nextCycleEnd.plusDays(1)
        }

        val allDates = predictedCycles.map { LocalDate.parse(it.date) }
        val noInfoDays = existingDays.filter { LocalDate.parse(it.date) !in allDates }
            .map { it.copy(dayCategory = DayCategory.NO_INFO, cycleId = 0) }

        dayRepository.upsertDays(predictedCycles + noInfoDays)
        cycleRepository.updateCyclesLastModifiedAt(cyclesToUpdate, LocalDateTime.now().toString())
    }

    suspend fun findOrMergeCycle(
        cycleRepository: CycleRepository,
        existingCycles: List<Cycle>,
        cycleStart: LocalDate,
        cycleEnd: LocalDate
    ): Int {
        val overlappingCycle = existingCycles.find {
            val start = LocalDate.parse(it.startDate)
            val end = LocalDate.parse(it.endDate)
            cycleStart <= end && cycleEnd >= start
        }

        return if (overlappingCycle != null) {
            val updatedCycle = overlappingCycle.copy(
                startDate = minOf(
                    LocalDate.parse(overlappingCycle.startDate),
                    cycleStart
                ).toString(),
                endDate = maxOf(LocalDate.parse(overlappingCycle.endDate), cycleEnd).toString(),
                lastModifiedAt = LocalDateTime.now().toString()
            )
            cycleRepository.updateCycle(updatedCycle)
            updatedCycle.id
        } else {
            cycleRepository.insertCycle(
                Cycle(
                    startDate = cycleStart.toString(),
                    endDate = cycleEnd.toString(),
                    status = CycleStatus.ACTIVE,
                    lastModifiedAt = LocalDateTime.now().toString()
                )
            ).toInt()
        }
    }

    private fun calculateAverageCycleLength(
        periods: List<Pair<LocalDate, LocalDate>>,
        context: Context
    ): Int {
        val cycleLengths = periods.zipWithNext<Pair<LocalDate, LocalDate>, Int> { current, next ->
            val d = next.first.toEpochDay() - current.first.toEpochDay()
            return d.toInt()
        }
        Log.d("AverageCycleLength", cycleLengths.toString())
        return if (cycleLengths.isNotEmpty()) cycleLengths.average().toInt() else
            EncryptedPrefsUtils.getCycleLength(context)
    }

    private fun calculateAveragePeriodLength(
        periods: List<Pair<LocalDate, LocalDate>>,
        context: Context
    ): Int {
        val periodLengths = periods.map { it.second.toEpochDay() - it.first.toEpochDay() + 1 }
        return if (periodLengths.isNotEmpty()) periodLengths.average().toInt() else
            EncryptedPrefsUtils.getPeriodLength(context)
    }

    private fun generateCycleDays(
        cycleStart: LocalDate,
        cycleEnd: LocalDate,
        periodStart: LocalDate,
        periodEnd: LocalDate,
        ovulationDay: LocalDate,
        existingDays: List<Day>,
        cycleId: Int
    ): List<Day> {
        val days = mutableListOf<Day>()
        var currentDate = cycleStart

        while (!currentDate.isAfter(cycleEnd)) {
            val existingDay = existingDays.find { LocalDate.parse(it.date) == currentDate }
            val dayCategory = when {
                currentDate in periodStart..periodEnd -> DayCategory.CONFIRMED_PERIOD
                currentDate == ovulationDay -> DayCategory.OVULATION
                currentDate.isAfter(ovulationDay.minusDays(5)) && currentDate.isBefore(ovulationDay) -> DayCategory.FERTILE
                else -> DayCategory.ORDINARY
            }

            val day = existingDay?.copy(
                cycleId = cycleId,
                dayCategory = dayCategory,
                lastModifiedAt = LocalDateTime.now().toString(),
                isStartOfPeriod = currentDate == periodStart,
                isEndOfPeriod = currentDate == periodEnd,
                daysToNextCycle = calculateDaysBetween(currentDate, cycleEnd.plusDays(1)),
                daysToOvulation = calculateDaysBetween(currentDate, ovulationDay),
                daysFromOvulation = calculateDaysBetween(ovulationDay, currentDate),
            ) ?: Day(
                cycleId = cycleId,
                date = currentDate.toString(),
                dayCategory = dayCategory,
                hydration = null,
                sleepHours = null,
                dailyNotes = listOf(),
                lastModifiedAt = LocalDateTime.now().toString(),
                isStartOfPeriod = currentDate == periodStart,
                isEndOfPeriod = currentDate == periodEnd,
                daysToNextCycle = calculateDaysBetween(currentDate, cycleEnd.plusDays(1)),
                daysToOvulation = calculateDaysBetween(currentDate, ovulationDay),
                daysFromOvulation = calculateDaysBetween(ovulationDay, currentDate),
            )

            days.add(day)
            currentDate = currentDate.plusDays(1)
        }
        return days
    }

    private suspend fun findOrCreateCycle(
        cycleRepository: CycleRepository,
        existingCycles: List<Cycle>,
        cycleStart: LocalDate,
        cycleEnd: LocalDate,
        existingDays: List<Day>
    ): Int {
        val overlappingCycle = existingCycles.find {
            LocalDate.parse(it.startDate) <= cycleEnd && LocalDate.parse(it.endDate) >= cycleStart
        }

        return if (overlappingCycle != null) {
            val newStartDate = minOf(cycleStart, LocalDate.parse(overlappingCycle.startDate))
            val newEndDate = maxOf(cycleEnd, LocalDate.parse(overlappingCycle.endDate))
            cycleRepository.updateCycle(
                overlappingCycle.copy(
                    startDate = newStartDate.toString(),
                    endDate = newEndDate.toString(),
                    lastModifiedAt = LocalDateTime.now().toString()
                )
            )
            overlappingCycle.id
        } else {
            cycleRepository.insertCycle(
                Cycle(
                    startDate = cycleStart.toString(),
                    endDate = cycleEnd.toString(),
                    status = CycleStatus.ACTIVE,
                    lastModifiedAt = LocalDateTime.now().toString()
                )
            ).toInt()
        }
    }

    private fun calculateDaysBetween(start: LocalDate, end: LocalDate?): Int? {
        return end?.let { (it.toEpochDay() - start.toEpochDay() + 1).toInt().takeIf { it >= 0 } }
    }
}

operator fun LocalDate.rangeTo(endInclusive: LocalDate) = generateSequence(this) {
    if (it.isBefore(endInclusive)) it.plusDays(1) else null
}