package com.example.coursework.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.coursework.CourseWorkApplication
import com.example.coursework.EncryptedPrefsUtils
import com.example.coursework.data.LoginRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface LoginUiState {
    object Loading: LoginUiState
    object Error: LoginUiState
    object Success: LoginUiState
}

class LoginViewModel(
    private val application: Application,
    private val loginRepository: LoginRepository,
): AndroidViewModel(application) {
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Loading)
    val uiState: StateFlow<LoginUiState> = _uiState

    fun loginWithIdToken(idToken: String) {
        viewModelScope.launch {
            _uiState.update{ LoginUiState.Loading }
            try {
                val result = loginRepository.loginWithGoogle(idToken)
                saveToken(result.accessToken)
                _uiState.update{ LoginUiState.Success }
            } catch (e: Exception) {
                _uiState.update{ LoginUiState.Error }
            }
        }
    }

    private fun saveToken(token: String) {
        EncryptedPrefsUtils.saveAccessToken(getApplication(), token)
    }

    fun getToken(): String? {
        return EncryptedPrefsUtils.getAccessToken(getApplication())
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as CourseWorkApplication)
                val loginRepository = application.appContainer.loginRepository
                LoginViewModel(application, loginRepository)
            }
        }
    }
}