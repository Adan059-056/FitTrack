package com.example.proyectoe.ui.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectoe.data.model.Workout
import com.example.proyectoe.R

@Composable
fun WorkoutInfo(workout: Workout){
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp))
    {
        Text(text="Intermediate",
            color = colorResource(R.color.orange),
            fontSize = 16.sp
            )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = workout.title,
            color = Color.White,
            fontSize = 23.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
            ){
            Text(workout.durationAll,color = Color.White, fontSize = 14.sp)
            Text(".", color = colorResource(R.color.orange), fontSize = 14.sp)
            Text("${workout.lessions.size} Exercise", color = Color.White, fontSize = 14.sp)
            Text(".", color = colorResource(R.color.orange), fontSize = 14.sp)
            Text("${workout.kcal} Kcal",color = Color.White, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = workout.description,
            color = Color.White,
            fontSize = 16.sp)
    }
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = "Exercise",
        color = Color.White,
        fontSize = 18.sp
    )
    Spacer(modifier = Modifier.height(8.dp))
}