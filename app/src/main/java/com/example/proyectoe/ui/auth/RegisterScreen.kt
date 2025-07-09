// src/main/java/com/example/proyectoe/ui/auth/RegisterScreen.kt
package com.example.proyectoe.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firestore.v1.Value

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit // Este es el callback para "volver al login"
) {
    val auth = FirebaseAuth.getInstance()
    var UserName by remember { mutableStateOf("") }
    var UserLastName by remember { mutableStateOf("") }
    var UserBirdDay by remember { mutableStateOf("") }
    var UserWeight by remember { mutableStateOf("") }
    var UserHigh by remember { mutableStateOf("") }
    var UserGender by remember { mutableStateOf("") }
    var UserSportActivity by remember { mutableStateOf("") }
    var UserObjetive by remember { mutableStateOf("") }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Crea tu cuenta de FitTrack", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = UserName,
            onValueChange = {UserName = it },
            label = { Text("Nombre") },
            singleLine = true
        )
        OutlinedTextField(
            value =UserLastName,
            onValueChange = {UserLastName = it},
            label = { Text("Apellidos") },
            singleLine = true
        )
        //Este texFiel hay que cambiarlo por un list selector con pesos definidos en Kg
        OutlinedTextField(
            value = UserHigh,
            onValueChange = {UserWeight = it},
            label = { Text("Peso(Kg)") },
            singleLine = true
        )
        OutlinedTextField(
            value = UserBirdDay,
            onValueChange = {UserBirdDay = it},
            label = { Text("Fecha de nacimiento") },
            singleLine = true
        )
        OutlinedTextField(
            value = UserGender,
            onValueChange = {UserGender = it},
            label = { Text("Genero") },
            singleLine = true
        )
        OutlinedTextField(
            value = UserHigh,
            onValueChange = {UserHigh = it},
            label = { Text("Altura") },
            singleLine = true
        )
        //Aqui va el nivel de actividad fisica en Nada, Bajo, Moderado, Alto.
        OutlinedTextField(
            value = UserSportActivity,
            onValueChange = { UserSportActivity= it},
            label = { Text("Nivel de actividad fisica") },
            singleLine = true
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmar Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (password != confirmPassword) {
                error = "Las contraseñas no coinciden"
            } else {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener { onRegisterSuccess() }
                    .addOnFailureListener { error = it.message }
            }
        }) {
            Text("Registrar")
        }

        error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onNavigateBack) { // Este TextButton es el que llamará a onNavigateBack
            Text("¿Ya tienes cuenta? Inicia sesión")
        }
    }
}