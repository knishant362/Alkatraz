package com.trendster.api.models.postinfo

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RequestInfo(
    val operation: String,
    val sql: String
)
