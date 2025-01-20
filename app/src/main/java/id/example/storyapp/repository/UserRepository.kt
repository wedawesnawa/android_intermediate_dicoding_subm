package id.example.storyapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import id.example.storyapp.data.UiState
import id.example.storyapp.local.preference.UserPreference
import id.example.storyapp.model.LoginResponse
import id.example.storyapp.model.RegisterResponse
import id.example.storyapp.model.UserModel
import id.example.storyapp.api.ApiService
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {

    fun userRegister(name: String, email: String, password: String): LiveData<UiState<RegisterResponse>> = liveData {
        emit(UiState.Loading)
        try {
            val response = apiService.register(name, email, password)
            if (response.error == false) {
                emit(UiState.Success(response))
            } else {
                emit(UiState.Error(response.message ?: "Registration failed"))
            }
        } catch (e: Exception) {
            when (e) {
                is SocketTimeoutException -> {
                    emit(UiState.Error("Connection timeout. Please try again."))
                }
                is UnknownHostException -> {
                    emit(UiState.Error("No internet connection"))
                }
                else -> {
                    emit(UiState.Error(e.message ?: "Unknown error occurred"))
                }
            }
        }
    }

    fun userLogin(email: String, password: String): LiveData<UiState<LoginResponse>> = liveData {
        emit(UiState.Loading)
        try {
            val response = apiService.login(email, password)
            if (response.error == false) {
                response.loginResult?.let { loginResult ->
                    userPreference.saveSession(
                        UserModel(
                            loginResult.name ?: "",
                            loginResult.token ?: "",
                            true
                        )
                    )
                }
                emit(UiState.Success(response))
            } else {
                emit(UiState.Error(response.message ?: "Login failed"))
            }
        } catch (e: Exception) {
            when (e) {
                is SocketTimeoutException -> {
                    emit(UiState.Error("Connection timeout. Please try again."))
                }
                is UnknownHostException -> {
                    emit(UiState.Error("No internet connection"))
                }
                else -> {
                    emit(UiState.Error(e.message ?: "Unknown error occurred"))
                }
            }
        }
    }
    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user.copy(isLogin = true))
    }
    fun getSession(): LiveData<UserModel> {
        return userPreference.getSession().asLiveData()
    }
    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }

}