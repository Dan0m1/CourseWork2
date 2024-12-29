package com.example.coursework.room.converters

import androidx.room.TypeConverter
import com.example.coursework.room.entities.DayCategory
import com.example.coursework.room.entities.DayNote
import com.example.coursework.room.entities.NoteCategory

class DayEntityFieldsConverter{
    @TypeConverter
    fun fromDayCategory(dayCategory: DayCategory): String {
        return dayCategory.name
    }

    @TypeConverter
    fun toDayCategory(categoryName: String): DayCategory {
        return DayCategory.valueOf(categoryName)
    }

    @TypeConverter
    fun fromArrayListDayNotes(dayNotes: List<DayNote>): String {
        return dayNotes.joinToString(separator = ",") { "${it.category.name}:${it.name}" }
    }

    @TypeConverter
    fun toArrayListDayNotes(dayNotes: String): List<DayNote> {
        return dayNotes.split(",").mapNotNull {
            val parts = it.split(":")
            if (parts.size == 2) {
                val (category, name) = parts
                DayNote(
                    category = NoteCategory.valueOf(category),
                    name = name
                )
            } else {
                null
            }
        }
    }
}