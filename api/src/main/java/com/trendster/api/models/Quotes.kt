package com.trendster.api.models


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Quotes(
    @Json(name = "author")
    val author: String,
    @Json(name = "__createdtime__")
    val createdtime: Long,
    @Json(name = "id")
    val id: String,
    @Json(name = "statement")
    val statement: String,
    @Json(name = "__updatedtime__")
    val updatedtime: Long
)