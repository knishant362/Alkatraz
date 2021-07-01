package com.trendster.api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ResponseMessage(
    @Json(name = "message")
    val message: String
)
