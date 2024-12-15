package com.example.coursework.ui.screens

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.coursework.GoogleSignInUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@Composable
fun LoginScreen(modifier: Modifier = Modifier,
                viewModel: LoginViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope().apply {
        Dispatchers.IO
    }
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
        GoogleSignInUtils.doGoogleSignIn(context, scope, null , viewModel::loginWithIdToken)
    }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        GoogleSignInUtils.doGoogleSignIn(
            context = context,
            scope = scope,
            launcher = launcher,
            login = viewModel::loginWithIdToken
        )
    }

    when (uiState) {
        is LoginUiState.Loading -> {
            CircularProgressIndicator()
        }
        is LoginUiState.Success -> {
            val token = viewModel.getToken()
            Text("Login Successful with token: $token")
        }
        is LoginUiState.Error -> {
            Text("Login Failed")
        }
    }
}