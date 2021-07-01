package com.trendster.harpic.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.trendster.api.models.*
import com.trendster.api.models.postinfo.MarkAttendanceInfo
import com.trendster.api.models.postinfo.RequestInfo
import com.trendster.harpic.data.Repository
import com.trendster.harpic.util.*
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.HashMap

class MainViewModel @ViewModelInject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    private val _predefinedHabitsResponse = MutableLiveData<NetworkResult<List<Habit>>>()
    val predefinedHabitsResponse get() = _predefinedHabitsResponse

    private val _quotesResponse = MutableLiveData<NetworkResult<List<Quotes>>>()
    val quotesResponse get() = _quotesResponse

    private val _userHabitsResponse = MutableLiveData<NetworkResult<List<SelectedHabits>>>()
    val userHabitsResponse get() = _userHabitsResponse

    private val _attendanceResponse = MutableLiveData<NetworkResult<ResponseMessage>>()
    val attendanceResponse get() = _attendanceResponse

    fun getPredefinedHabits() = viewModelScope.launch {
        val info = RequestInfo(
            "sql",
            "SELECT * FROM habit.predefined_habits"
        )
        safeCallPredefinedHabits(info)
    }

    private suspend fun safeCallPredefinedHabits(info: RequestInfo) {
        _predefinedHabitsResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.fetchPredefinedHabits(info)
                _predefinedHabitsResponse.value = handleHabitsResponse(response)
            } catch (e: Exception) {
                _predefinedHabitsResponse.value = NetworkResult.Error("Habits not found")
            }
        } else {
            _predefinedHabitsResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    private fun handleHabitsResponse(response: Response<List<Habit>>): NetworkResult<List<Habit>> {
        when {
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error("Timeout")
            }
            response.code() == 402 -> {
                return NetworkResult.Error("API Key Limited")
            }
            response.body()!!.isNullOrEmpty() -> {
                return NetworkResult.Error("Habits not found")
            }
            response.isSuccessful -> {
                return NetworkResult.Success(response.body())
            }
            else -> {
                return NetworkResult.Error(response.message())
            }
        }
    }

    private fun handleSelectedHabitsResponse(response: Response<List<SelectedHabits>>): NetworkResult<List<SelectedHabits>> {
        when {
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error("Timeout")
            }
            response.code() == 402 -> {
                return NetworkResult.Error("API Key Limited")
            }
            response.body()!!.isNullOrEmpty() -> {
                return NetworkResult.Error("Habits not found")
            }
            response.isSuccessful -> {
                return NetworkResult.Success(response.body())
            }
            else -> {
                return NetworkResult.Error(response.message())
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

    fun fetchUserHabits(uid: String) = viewModelScope.launch {
        val info = RequestInfo(
            "sql",
            "SELECT * FROM user_data.$uid"
        )
        safeCallUserHabits(info)
    }

    private suspend fun safeCallUserHabits(info: RequestInfo) {
        _userHabitsResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.fetchUserHabits(info)
                _userHabitsResponse.value = handleSelectedHabitsResponse(response)
            } catch (e: Exception) {
                _userHabitsResponse.value = NetworkResult.Error(e.message)
            }
        } else {
            _userHabitsResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    fun markTodayAttendance(uid: String, newData: HashMap<String, Any>) = viewModelScope.launch {
        val attendanceData = MarkAttendanceInfo.AttendanceData(
            newData[NAME] as String,
            newData[REMAINING] as Int,
            newData[DAYS] as String,
            newData[NEXT_DATE] as DateFormat
        )
        val info = MarkAttendanceInfo(
            "update",
            "user_data",
            uid,
            listOf(attendanceData)
        )
        safeCallMarkTodayAttendance(info)
    }

    private suspend fun safeCallMarkTodayAttendance(info: MarkAttendanceInfo) {
        _attendanceResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.markTodayAttendance(info)
                _attendanceResponse.value = handleMarkAttendanceResponse(response)
            } catch (e: Exception) {
                _attendanceResponse.value = NetworkResult.Error(e.message)
            }
        } else {
            _attendanceResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    private fun handleMarkAttendanceResponse(response: Response<ResponseMessage>): NetworkResult<ResponseMessage> {
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

    fun fetchQuotes() = viewModelScope.launch {
        val info = RequestInfo(
            "sql",
            "SELECT * FROM habit.quotes"
        )
        safeCallFetchQuotes(info)
    }

    private suspend fun safeCallFetchQuotes(info: RequestInfo) {
        _quotesResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.fetchQuotes(info)
                _quotesResponse.value = handleQuotesResponse(response)
            } catch (e: Exception) {
                _quotesResponse.value = NetworkResult.Error(e.message)
            }
        }
    }

    private fun handleQuotesResponse(response: Response<List<Quotes>>): NetworkResult<List<Quotes>> {
        when {
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error("Timeout")
            }
            response.code() == 402 -> {
                return NetworkResult.Error("API Key Limited")
            }
            response.body()!!.isNullOrEmpty() -> {
                return NetworkResult.Error("Quotes not found")
            }
            response.isSuccessful -> {
                return NetworkResult.Success(response.body())
            }
            else -> {
                return NetworkResult.Error(response.message())
            }
        }
    }
}
