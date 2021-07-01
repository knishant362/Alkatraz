package com.trendster.api.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(
    val id: String,
    val name: String,
)
