package com.example.proyectoe.ui.dashboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyectoe.data.datasource.WorkoutDataProvider.getData
import com.example.proyectoe.ui.theme.ProyectoETheme
import com.example.proyectoe.ui.dashboard.components.MainBottonBar
import com.example.proyectoe.ui.dashboard.MainContent

class MainActivity : ComponentActivity() {
    private val workouts = getData()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

                Scaffold(
                    containerColor = Color(0Xff101322),
                    bottomBar = {MainBottonBar()},
                    ) { innerPadding ->
                    MainContent(modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                        workouts = workouts)
                }

        }
    }
}
