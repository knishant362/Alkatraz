package com.trendster.api.models.postinfo

import com.squareup.moshi.JsonClass
import com.trendster.api.models.User

@JsonClass(generateAdapter = true)
class CreateUserInfo(
    val operation: String,
    val schema: String,
    val table: String,
    val records: List<User>
)
