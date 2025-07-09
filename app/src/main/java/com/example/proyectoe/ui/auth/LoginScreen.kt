package com.example.proyectoe.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth



// Definición de colores a nivel de archivo para que sean accesibles globalmente en este archivo
private val BackgroundColor = Color(0xFF0F172A) // Fondo azul oscuro
private val CardColor = Color(0xFF1A2C50)       // Tarjetas azul medio
private val PrimaryColor = Color(0xFFF97316)     // Naranja brillante para botones
private val SecondaryColor = Color(0xFFFFFFFF)   // Blanco para textos
private val ErrorColor = Color(0xFFFF6B6B)       // Rojo suave para errores
private val BorderColor = Color(0xFF3A506B)      // Borde azul grisáceo



@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    //onNavigateToRegister: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var fieldErrors by remember { mutableStateOf(setOf<String>()) }
    var isProcessing by remember { mutableStateOf(false) }

    fun validateFields(): Boolean {
        val errors = mutableSetOf<String>()

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errors.add("email")
        }

        if (password.length < 6) {
            errors.add("password")
            errorMessage = "La contraseña debe tener al menos 6 caracteres"
        }

        fieldErrors = errors
        if (errors.isNotEmpty()) {
            errorMessage = "Por favor, completa todos los campos requeridos correctamente."
        } else {
            errorMessage = null
        }
        return errors.isEmpty()
    }


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp) // Espaciado entre secciones
    ) {
        item {
            Text(
                "Iniciar Sesión",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)
            )
        }
        item {
            SectionCard(title = "Ingresa tus datos", cardColor = CardColor) {
                SimpleTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Correo electrónico",
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                    isError = fieldErrors.contains("email"),
                    textColor = SecondaryColor,
                    borderColor = BorderColor
                )
                Divider(
                    color = BorderColor,
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                PasswordField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Contraseña",
                    isError = fieldErrors.contains("password"),
                    textColor = SecondaryColor,
                    borderColor = BorderColor
                )


                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener { onLoginSuccess() }
                        .addOnFailureListener { error = it.message }
                }) {
                    Text("Ingresar")
                }

                error?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(it, color = MaterialTheme.colorScheme.error)
                }

                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = onNavigateBack) {
                    Text("¿No tienes cuenta? Regístrate")
                }
            }
        }
    }
}

// Aqui es donde vsa la logica de inicio de secion