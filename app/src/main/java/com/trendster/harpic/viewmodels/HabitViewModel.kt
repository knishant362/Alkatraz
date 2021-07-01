package com.trendster.harpic.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.trendster.api.models.DateFormat
import com.trendster.api.models.ResponseMessage
import com.trendster.api.models.postinfo.SaveUserHabitsInfo
import com.trendster.harpic.data.Repository
import com.trendster.harpic.model.HabitItem
import com.trendster.harpic.util.NetworkResult
import kotlinx.coroutines.launch
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class HabitViewModel @ViewModelInject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

//    var habitData: MutableLiveData<HabitItem> = MutableLiveData()

    private val _savingResponse = MutableLiveData<NetworkResult<ResponseMessage>>()
    val savingResponse get() = _savingResponse

    private val _addingHabitResponse = MutableLiveData<NetworkResult<ResponseMessage>>()
    val addingHabitResponse get() = _addingHabitResponse

    fun createUserHabit(
        userUID: String,
        habitName: String,
        goal: String,
        reward: String,
        note: String,
        habitDuration: Int
    ) = viewModelScope.launch {

        Log.d("Hele", userUID)
        Log.d("Hele", habitName)
        Log.d("Hele", goal)
        Log.d("Hele", reward)
        Log.d("Hele", note)
        Log.d("Hele", habitDuration.toString())

        val date = todayDay()

        val userHabit = SaveUserHabitsInfo.HabitData(
            habitName,
            goal,
            note,
            reward,
            habitDuration,
            habitDuration,
            "0000000000000000000000000000000",
            date,
            date,
            date
        )

        val info = SaveUserHabitsInfo(
            "insert",
            "user_data",
            userUID,
            listOf(userHabit)
        )
        safeCallSaveUserHabit(info)
    }

    fun todayDay(): DateFormat {
        val today = Calendar.getInstance()
        val date = today.time
        Log.d("myTime", date.toString())
        Log.d("myTime", today.toString())
        val day = SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.time)

        /**individual item */
        val myday = today.get(Calendar.DATE)
        val month = today.get(Calendar.MONTH)
        val year = today.get(Calendar.YEAR)

        Log.d("myTime11", myday.toString())
        /**individual item */

        return DateFormat(myday, month + 1, year)
    }

    private suspend fun safeCallSaveUserHabit(info: SaveUserHabitsInfo) {
        _savingResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.createUserHabit(info)
                _savingResponse.value = handleSavingResponse(response)
            } catch (e: Exception) {
                _savingResponse.value = NetworkResult.Error(e.message)
            }
        } else {
            _savingResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    private fun handleSavingResponse(response: Response<ResponseMessage>): NetworkResult<ResponseMessage> {
        return when {
            response.message().toString().contains("timeout") -> {
                NetworkResult.Error("Timeout")
            }
            response.message().toString().contains("inserted 0 of 1 records") -> {
                return NetworkResult.Error("This Habit is already started.")
            }
            response.code() == 402 -> {
                NetworkResult.Error("API Key Limited")
            }
            response.isSuccessful -> {
                NetworkResult.Success(response.body())
            }
            else -> {
                NetworkResult.Error(response.message())
            }
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectionManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNetwork = connectionManager.activeNetwork ?: return false
        val capabilities = connectionManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    fun addHabitToUserHabits(userUID: String, habit: HabitItem) = viewModelScope.launch {

        val date = todayDay()

        val userHabit = SaveUserHabitsInfo.HabitData(
            habit.name,
            habit.goal,
            habit.note,
            habit.reward,
            habit.duration,
            habit.duration,
            "0000000000000000000000000000000",
            date,
            date,
            date
        )

        val info = SaveUserHabitsInfo(
            "insert",
            "user_data",
            userUID,
            listOf(userHabit)
        )
        safeCallAddHabitToUserHabits(info)
    }

    private suspend fun safeCallAddHabitToUserHabits(info: SaveUserHabitsInfo) {
        _addingHabitResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.addHabitToUserHabits(info)
                _addingHabitResponse.value = handleSavingResponse(response)
            } catch (e: Exception) {
                _addingHabitResponse.value = NetworkResult.Error(e.message)
            }
        } else {
            _addingHabitResponse.value = NetworkResult.Error("No Internet Connection")
        }
    }
}
