package com.example.coursework.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.coursework.ui.screens.login.LoginViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.coursework.ui.navigation.CourseWorkNavGraph
import com.example.coursework.ui.screens.login.LoginScreen

@Composable
fun CourseWorkApp(modifier:Modifier = Modifier, navController: NavHostController = rememberNavController()) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier.fillMaxSize()
    ) {
        CourseWorkNavGraph(navHostController = navController, Modifier.fillMaxSize())
    }
}