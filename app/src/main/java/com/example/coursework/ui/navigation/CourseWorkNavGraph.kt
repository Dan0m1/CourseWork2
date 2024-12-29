package com.example.coursework.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.coursework.ui.screens.calendar.CalendarScreen
import com.example.coursework.ui.screens.calendar.CalendarScreenViewModel
import com.example.coursework.ui.screens.calendar.MenstrualCalendarDestination

import com.example.coursework.ui.screens.home.HomeDestination
import com.example.coursework.ui.screens.home.HomeScreen
import com.example.coursework.ui.screens.home.HomeViewModel

import com.example.coursework.ui.screens.login.LoginDestination
import com.example.coursework.ui.screens.login.LoginScreen
import com.example.coursework.ui.screens.login.LoginViewModel

@Composable
fun CourseWorkNavGraph(
    navHostController: NavHostController,
    modifier: Modifier
) {
    NavHost(
        navController = navHostController,
        startDestination = LoginDestination.route,
        modifier = modifier
    ) {
        composable(
            route = LoginDestination.route
        ) {
            val viewModel = hiltViewModel<LoginViewModel>()
            LoginScreen(
                onLogin = { navHostController.navigate(HomeDestination.route) },
                viewModel = viewModel
            )
        }
        composable(
            route = HomeDestination.route
        ) {
            val viewModel = hiltViewModel<HomeViewModel>()
            HomeScreen(
                viewModel = viewModel,
                onAddPeriodClick = { navHostController.navigate(MenstrualCalendarDestination.route) }
            )
        }
        composable(
            route = MenstrualCalendarDestination.route
        ) {
            val viewModel = hiltViewModel<CalendarScreenViewModel>()
            CalendarScreen(
                viewModel = viewModel,
                onCancel = { navHostController.popBackStack() }
            )
        }
    }
}