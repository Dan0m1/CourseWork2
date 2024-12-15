package com.example.coursework

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.compose.CourseWorkTheme
import com.example.coursework.ui.CourseWorkApp
import com.example.coursework.ui.screens.LoginScreen
import com.example.coursework.ui.screens.LoginViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CourseWorkTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CourseWorkApp(Modifier.padding(innerPadding))
                }
            }
        }
    }
}
