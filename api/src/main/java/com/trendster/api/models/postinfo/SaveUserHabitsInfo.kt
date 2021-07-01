package com.trendster.api.models.postinfo

import com.squareup.moshi.JsonClass
import com.trendster.api.models.DateFormat

@JsonClass(generateAdapter = true)
data class SaveUserHabitsInfo(
    val operation: String,
    val schema: String,
    val table: String,
    val records: List<HabitData>
) {
    @JsonClass(generateAdapter = true)
    data class HabitData(
        val name: String,
        val goal: String,
        val reward: String,
        val note: String,
        val duration: Int,
        val remaining: Int,
        val days: String,
        val startDate: DateFormat,
        val endDate: DateFormat,
        val nextDate: DateFormat
    )
}
