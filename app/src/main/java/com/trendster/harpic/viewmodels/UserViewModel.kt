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
import com.trendster.api.models.postinfo.CreateUserHabitTableInfo
import com.trendster.api.models.postinfo.CreateUserInfo
import com.trendster.api.models.postinfo.OperationInfo
import com.trendster.harpic.data.Repository
import com.trendster.harpic.util.NetworkResult
import kotlinx.coroutines.launch
import retrofit2.Response

class UserViewModel @ViewModelInject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    private val _userCheckResponse = MutableLiveData<NetworkResult<List<User>>>()
    val userRegistrationCheckResponse get() = _userCheckResponse

    private val _userCreateResponse = MutableLiveData<NetworkResult<ResponseMessage>>()
    val userCreateResponse get() = _userCreateResponse

    fun createUser(uid: String, name: String) = viewModelScope.launch {
        val info = CreateUserInfo(
            "insert",
            "habit",
            "users",
            listOf(
                User(
                    uid,
                    name
                )
            )
        )
        val tableInfo = CreateUserHabitTableInfo(
            "create_table",
            "user_data",
            uid,
            "id"
        )
        safeCallCreateUser(info, tableInfo)
    }

    private suspend fun safeCallCreateUser(info: CreateUserInfo, tableInfo: CreateUserHabitTableInfo) {
        _userCreateResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.createUser(info)
                if (response.isSuccessful) {
                    repository.remote.createUserHabitsTable(tableInfo)
                    _userCreateResponse.value = handleUserCreateResponse(response)
                }
            } catch (e: Exception) {
                _userCreateResponse.value = NetworkResult.Error(e.message)
            }
        } else {
            _userCreateResponse.value = NetworkResult.Error("No Internet Connection")
        }
    }

    private fun handleUserCreateResponse(response: Response<ResponseMessage>): NetworkResult<ResponseMessage>? {
        return when {
            response.message().toString().contains("timeout") -> {
                NetworkResult.Error("Timeout")
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

    fun checkRegistration(uid: String) = viewModelScope.launch {
        val info = OperationInfo(
            "search_by_value",
            "habit",
            "users",
            "id",
            uid,
            arrayOf("*")
        )
        safeCallUserRegistration(info)
    }

    private suspend fun safeCallUserRegistration(info: OperationInfo) {
        _userCheckResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.checkRegistration(info)
                _userCheckResponse.value = handleUserCheckResponse(response)
            } catch (e: Exception) {
            }
        } else {
            _userCheckResponse.value = NetworkResult.Error("No Internet Connection. ")
        }
    }

    private fun handleUserCheckResponse(response: Response<List<User>>): NetworkResult<List<User>> {
        when {
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error("Timeout")
            }
            response.code() == 402 -> {
                return NetworkResult.Error("API Key Limited")
            }
            response.body()!!.isNullOrEmpty() -> {
                return NetworkResult.Error("User not Registered")
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
}
