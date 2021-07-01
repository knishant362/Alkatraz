package com.trendster.api

import com.trendster.api.models.postinfo.CreateUserInfo
import com.trendster.api.models.postinfo.RequestInfo
import com.trendster.api.models.User
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Test

class HarperClientTests {

    private val harperClient = HarperClient()

    @Test
    fun `POST habits`() {
        runBlocking {
            val info = RequestInfo(
                "sql",
                "SELECT * FROM habit.predefined_habits"
            )
            val habits = harperClient.apiService.fetchPredefinedHabits(info)
            assertNotNull(habits.body())
        }
    }
    @Test
    fun `POST Create User`() {
        runBlocking {
            val info = CreateUserInfo(
                "insert",
                "habit",
                "users",
                listOf(
                    User(
                        "test_uid",
                        "test_name"
                    )
                )
            )
            val user = harperClient.apiService.createUser(info)
            assertNotNull(user.body())
        }
    }
}
