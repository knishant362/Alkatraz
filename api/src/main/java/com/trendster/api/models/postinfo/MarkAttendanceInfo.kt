package com.trendster.api.models.postinfo

import com.squareup.moshi.JsonClass
import com.trendster.api.models.DateFormat

@JsonClass(generateAdapter = true)
data class MarkAttendanceInfo(
    val operation: String,
    val schema: String,
    val table: String,
    val records: List<AttendanceData>
) {
    @JsonClass(generateAdapter = true)
    data class AttendanceData(
        val name: String,
        val remaining: Int,
        val days: String,
        val nextDate: DateFormat
    )
}
