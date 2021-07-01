package com.trendster.api.services

import com.trendster.api.models.*
import com.trendster.api.models.postinfo.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface HarperAPI {

    @POST(".")
    suspend fun fetchPredefinedHabits(
        @Body info: RequestInfo
    ): Response<List<Habit>>

    @POST(".")
    suspend fun checkRegistration(
        @Body info: OperationInfo
    ): Response<List<User>>

    @POST(".")
    suspend fun createUser(
        @Body info: CreateUserInfo
    ): Response<ResponseMessage>

    @POST(".")
    suspend fun createUserHabit(
        @Body userHabitInfo: SaveUserHabitsInfo
    ): Response<ResponseMessage>

    @POST(".")
    suspend fun createUserHabitTable(
        @Body tableInfo: CreateUserHabitTableInfo
    )

    @POST(".")
    suspend fun fetchUserHabits(
        @Body info: RequestInfo
    ): Response<List<SelectedHabits>>

    @POST(".")
    suspend fun addHabitToUserHabits(
        @Body info: SaveUserHabitsInfo
    ): Response<ResponseMessage>

    @POST(".")
    suspend fun markTodayAttendance(
        @Body info: MarkAttendanceInfo
    ): Response<ResponseMessage>

    @POST(".")
    suspend fun fetchQuotes(
        @Body info: RequestInfo
    ): Response<List<Quotes>>
}
