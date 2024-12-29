package com.example.coursework.ui.screens.login

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.coursework.GoogleSignInUtils
import com.example.coursework.ui.navigation.NavigationDestination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object LoginDestination : NavigationDestination {
    override val route: String = "login"
}

@Composable
fun LoginScreen(
    onLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope { Dispatchers.IO }
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
        GoogleSignInUtils.doGoogleSignIn(context, scope, null , viewModel::loginWithIdToken)
    }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect("google_login") {
        withContext(Dispatchers.IO) {
            GoogleSignInUtils.doGoogleSignIn(
                context = context,
                scope = scope,
                launcher = launcher,
                login = viewModel::loginWithIdToken
            )
        }
    }

    when (uiState) {
        is LoginUiState.Loading -> {
            Surface(Modifier.fillMaxSize().padding(16.dp)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(200.dp)
                    ,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
        is LoginUiState.Success -> {
            val token = viewModel.getToken()
            Surface(Modifier.fillMaxSize().padding(16.dp)) {
                Box(Modifier.fillMaxWidth()) {
                    Text(
                        "Login Successful with token: $token",
                        modifier = Modifier.align(alignment = Alignment.Center)
                    )
                }
            }
            LaunchedEffect("login_delay") {
                delay(200)
                onLogin()
            }

        }
        is LoginUiState.Error -> {
            Surface(Modifier.fillMaxSize().padding(16.dp)) {
                Box(Modifier.fillMaxWidth()) {
                    Text("Login Failed",
                        modifier = Modifier.align(alignment = Alignment.Center)
                    )
                }
            }

        }
    }
}