package com.trendster.harpic.model

import java.io.Serializable

data class HabitItem(
    val createdtime: Long,
    val goal: String,
    val id: String,
    val name: String,
    val note: String,
    val reward: String,
    val duration: Int,
    val remaining: Int
) : Serializable
