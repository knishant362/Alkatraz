package com.trendster.api.models.postinfo

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class OperationInfo(
    val operation: String,
    val schema: String,
    val table: String,
    val search_attribute: String,
    val search_value: String,
    val get_attributes: Array<String>
)
