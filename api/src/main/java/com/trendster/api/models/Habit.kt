package com.trendster.api.models


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Habit(
    @Json(name = "__createdtime__")
    val createdtime: Long,
    @Json(name = "goal")
    val goal: String,
    @Json(name = "id")
    val id: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "note")
    val note: String,
    @Json(name = "reward")
    val reward: String,
    @Json(name = "duration")
    val duration: Int,
    @Json(name = "remaining")
    val remaining: Int
)