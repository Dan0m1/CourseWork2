package com.example.coursework.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.coursework.ui.screens.LoginViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coursework.ui.screens.LoginScreen

@Composable
fun CourseWorkApp(modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxSize()
    ) {
        val loginViewModel: LoginViewModel = viewModel(factory = LoginViewModel.Factory)
        LoginScreen(
            viewModel = loginViewModel,
            modifier = modifier
        )
    }
}