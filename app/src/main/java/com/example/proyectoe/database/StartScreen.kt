package com.example.proyectoe.database

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun StartScreen(
    onUserLoggedIn: () -> Unit,
    onUserNotLoggedIn: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()

    LaunchedEffect(Unit) {
        val user = auth.currentUser
        if (user != null) {
            onUserLoggedIn()
        } else {
            onUserNotLoggedIn()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color.White,
            strokeWidth = 4.dp,
            modifier = Modifier.size(40.dp)
        )
    }
}