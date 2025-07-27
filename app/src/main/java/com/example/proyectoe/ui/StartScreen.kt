package com.example.proyectoe.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

/**
 * Composable de la pantalla de inicio/splash que verifica el estado de autenticación de Firebase.
 *
 * @param onUserLoggedIn Callback que se invoca si el usuario ya ha iniciado sesión.
 * @param onUserNotLoggedIn Callback que se invoca si el usuario no ha iniciado sesión.
 */
@Composable
fun StartScreen(
    onUserLoggedIn: () -> Unit,
    onUserNotLoggedIn: () -> Unit
) {
    // LaunchedEffect se ejecuta una vez cuando el Composable entra en la composición
    // y se limpia cuando sale de la composición.
    // Usamos `Unit` como clave para que se ejecute solo una vez.
    LaunchedEffect(Unit) {
        // Obtiene la instancia de FirebaseAuth
        val auth = FirebaseAuth.getInstance()

        // Verifica si hay un usuario actualmente autenticado
        if (auth.currentUser != null) {
            // Si hay un usuario, invoca el callback de usuario logueado
            onUserLoggedIn()
        } else {
            // Si no hay usuario, invoca el callback de usuario no logueado
            onUserNotLoggedIn()
        }
    }

    // UI simple para mostrar mientras se verifica el estado de autenticación
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF101322) // Color de fondo consistente con tu MainActivity
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(64.dp),
                color = MaterialTheme.colorScheme.primary // Puedes ajustar el color
            )
            Text(
                text = "Cargando...",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

/**
 * Función de vista previa para StartScreen.
 * Permite visualizar el Composable en el editor de Android Studio.
 */
@Preview(showBackground = true)
@Composable
fun StartScreenPreview() {
    MaterialTheme {
        StartScreen(
            onUserLoggedIn = { /* Do nothing for preview */ },
            onUserNotLoggedIn = { /* Do nothing for preview */ }
        )
    }
}
