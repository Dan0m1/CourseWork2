package com.example.coursework.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.coursework.room.converters.DayEntityFieldsConverter
import com.example.coursework.room.daos.CycleDao
import com.example.coursework.room.daos.DayDao
import com.example.coursework.room.entities.Cycle
import com.example.coursework.room.entities.Day

@Database(
    entities = [Day::class, Cycle::class],
    version = 8,
    exportSchema = false
)
@TypeConverters(DayEntityFieldsConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getDaysDao(): DayDao
    abstract fun getCycleDao(): CycleDao
}