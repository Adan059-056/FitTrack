package com.example.proyectoe.Activity.WorkoutActivity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.proyectoe.Model.Lession

@Composable
fun ExerciseList(lessions:List<Lession>){
    Column (verticalArrangement = Arrangement.spacedBy(12.dp)){
        lessions.forEach { lessions->
        LessonRow(lessions)
        }
    }
}