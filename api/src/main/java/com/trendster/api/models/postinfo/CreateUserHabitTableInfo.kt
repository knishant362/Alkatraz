package com.trendster.api.models.postinfo

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class CreateUserHabitTableInfo(
    val operation: String,
    val schema: String,
    val table: String,
    val hash_attribute: String
)
