package com.trendster.api.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DateFormat(
    val date: Int,
    val month: Int,
    val year: Int
)
