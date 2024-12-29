package com.example.coursework.ui.screens.login

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.coursework.CourseWorkApplication
import com.example.coursework.EncryptedPrefsUtils
import com.example.coursework.data.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface LoginUiState {
    object Loading: LoginUiState
    object Error: LoginUiState
    object Success: LoginUiState
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val loginRepository: LoginRepository,
): ViewModel() {
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Loading)
    val uiState: StateFlow<LoginUiState> = _uiState

    fun loginWithIdToken(idToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update{ LoginUiState.Loading }
            try {
                val result = loginRepository.loginWithGoogle(idToken)
                saveToken(result.accessToken)
                _uiState.update{ LoginUiState.Success }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update{ LoginUiState.Error }
            }
        }
    }

    private fun saveToken(token: String) {
        EncryptedPrefsUtils.saveAccessToken(context.applicationContext, token)
    }

    fun getToken(): String? {
        return EncryptedPrefsUtils.getAccessToken(context.applicationContext)
    }

//    companion object {
//        val Factory: ViewModelProvider.Factory = viewModelFactory {
//            initializer {
//                val application = (this[APPLICATION_KEY] as CourseWorkApplication)
//                val loginRepository = application.appContainer.loginRepository
//                LoginViewModel(application, loginRepository)
//            }
//        }
//    }
}