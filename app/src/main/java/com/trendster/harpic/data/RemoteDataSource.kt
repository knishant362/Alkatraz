package com.trendster.harpic.data

import com.trendster.api.models.*
import com.trendster.api.models.postinfo.*
import com.trendster.api.models.postinfo.CreateUserHabitTableInfo
import com.trendster.api.services.HarperAPI
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val harperAPI: HarperAPI
) {
    suspend fun fetchPredefinedHabits(info: RequestInfo): Response<List<Habit>> {
        return harperAPI.fetchPredefinedHabits(info)
    }

    suspend fun checkRegistration(info: OperationInfo): Response<List<User>> {
        return harperAPI.checkRegistration(info)
    }

    suspend fun createUser(info: CreateUserInfo): Response<ResponseMessage> {
        return harperAPI.createUser(info)
    }

    suspend fun createUserHabit(info: SaveUserHabitsInfo): Response<ResponseMessage> {
        return harperAPI.createUserHabit(info)
    }

    suspend fun createUserHabitsTable(tableInfo: CreateUserHabitTableInfo) {
        return harperAPI.createUserHabitTable(tableInfo)
    }

    suspend fun fetchUserHabits(info: RequestInfo): Response<List<SelectedHabits>> {
        return harperAPI.fetchUserHabits(info)
    }

    suspend fun addHabitToUserHabits(info: SaveUserHabitsInfo): Response<ResponseMessage> {
        return harperAPI.addHabitToUserHabits(info)
    }

    suspend fun markTodayAttendance(info: MarkAttendanceInfo): Response<ResponseMessage> {
        return harperAPI.markTodayAttendance(info)
    }

    suspend fun fetchQuotes(info: RequestInfo): Response<List<Quotes>> {
        return harperAPI.fetchQuotes(info)
    }
}
