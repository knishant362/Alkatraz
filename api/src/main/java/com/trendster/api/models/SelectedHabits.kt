package com.trendster.api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SelectedHabits(
    @Json(name = "__createdtime__")
    val createdtime: Long,
    @Json(name = "days")
    val days: String,
    @Json(name = "duration")
    val duration: Int,
    @Json(name = "endDate")
    val endDate: DateFormat,
    @Json(name = "goal")
    val goal: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "nextDate")
    val nextDate: DateFormat,
    @Json(name = "note")
    val note: String,
    @Json(name = "remaining")
    val remaining: Int,
    @Json(name = "reward")
    val reward: String,
    @Json(name = "startDate")
    val startDate: DateFormat,
    @Json(name = "__updatedtime__")
    val updatedtime: Long
)
