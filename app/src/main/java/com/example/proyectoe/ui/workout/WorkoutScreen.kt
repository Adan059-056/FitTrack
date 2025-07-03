package com.example.proyectoe.ui.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.proyectoe.data.model.Workout
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.proyectoe.ui.workout.componets.WorkoutHeader
import com.example.proyectoe.ui.workout.componets.StartWorkoutButton

@Composable
fun WorkoutScreen(
    workout: Workout,
    onBack:()->Unit,
    onStart:()->Unit
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xff101322))
    ){
        Box(modifier = Modifier.weight(1f))
        {
            Column (modifier = Modifier.verticalScroll(rememberScrollState())) {
                WorkoutHeader(workout,onBack)
                WorkoutInfo(workout)
                ExerciseList(workout.lessions)
            }
        }
        StartWorkoutButton (onStart)
    }
}