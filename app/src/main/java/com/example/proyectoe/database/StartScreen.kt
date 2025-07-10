package com.example.proyectoe.database

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun StartScreen(
    onUserLoggedIn: () -> Unit,
    onUserNotLoggedIn: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    var isChecking by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(100) // Permite que Compose y Firebase se inicialicen completamente
        val user = auth.currentUser
        if (user != null) {
            onUserLoggedIn()
        } else {
            onUserNotLoggedIn()
        }
        isChecking = false
    }

    if (isChecking) {
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
}