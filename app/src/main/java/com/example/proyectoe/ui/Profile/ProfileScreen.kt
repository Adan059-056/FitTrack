package com.example.proyectoe.ui.Profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator // Importar para el indicador de carga
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState // Importar para observar StateFlow
import androidx.compose.runtime.getValue // Importar para descomponer el estado
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel // Importar para obtener la ViewModel
import com.example.proyectoe.R
import com.example.proyectoe.database.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen( viewModel: ProfileViewModel = viewModel(), // Inyecta la ViewModel
                   onBack: () -> Unit = {},
                   onLogout: () -> Unit = {}) {

    val user by viewModel.user.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Paleta de colores
    val darkBlue = Color(0xFF0A1128)
    val deepBlue = Color(0xFF0F1C3F)
    val navyBlue = Color(0xFF1A2C5C)
    val black = Color(0xFF000000)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil de Salud", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Atrás",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Cambiar objetivo */ }) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Cambiar Objetivo",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = onLogout) {
                        Icon(
                            Icons.Default.ExitToApp,
                            contentDescription = "Cerrar Sesión",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            deepBlue,
                            darkBlue,
                            black
                        ),
                        startY = 0f,
                        endY = 1000f
                    )
                )
                .padding(innerPadding) // Aplica el padding de Scaffold aquí
        ) {
            when {
                isLoading -> {
                    // Muestra un indicador de carga mientras se obtienen los datos
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = Color.White)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Cargando perfil...", color = Color.White)
                    }
                }
                errorMessage != null -> {
                    // Muestra un mensaje de error si algo salió mal
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Error: $errorMessage", color = Color.Red, fontSize = 18.sp)
                        // Opcional: un botón para reintentar
                        Button(onClick = { viewModel.loadUserProfile() }) {
                            Text("Reintentar")
                        }
                    }
                }
                user != null -> {
                    // Si los datos del usuario están disponibles, muestra el perfil
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // AQUÍ ES DONDE DEBES CORREGIR LAS LLAMADAS:
                        ProfileHeader(
                            cardColor = navyBlue.copy(alpha = 0.8f),
                            textColor = Color.White,
                            // *** PASA LOS DATOS REALES DEL USUARIO AQUÍ ***
                            userName = "${user?.nombre} ${user?.apellidos}", // Combina nombre y apellido
                            userEmail = user?.email ?: "N/A" // Usa el email del usuario
                        )

                        // Sección de información personal (pasamos los datos del usuario)
                        PersonalInfoSection(
                            cardColor = navyBlue.copy(alpha = 0.8f),
                            textColor = Color.White,
                            // *** PASA LOS DATOS REALES DEL USUARIO AQUÍ ***
                            fechaNacimiento = user?.fechaNacimiento ?: "N/A",
                            genero = user?.genero ?: "N/A",
                            estatura = "${user?.altura ?: "N/A"} cm", // Asegúrate de añadir " cm" si quieres
                            peso = "${user?.peso ?: "N/A"} kg",       // Asegúrate de añadir " kg" si quieres
                            actividad = user?.actividad ?: "N/A", // Pasa el nivel de actividad
                            objetivo = user?.objetivo ?: "N/A"
                        )
                    }
                }
                else -> {
                    // Caso para cuando no hay usuario ni error (puede que no haya iniciado sesión)
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No se pudo cargar el perfil. ¿Has iniciado sesión?", color = Color.White, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileHeader(
    cardColor: Color = MaterialTheme.colorScheme.surface,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    userName: String, // Parámetro para el nombre del usuario
    userEmail: String // Parámetro para el email del usuario
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.DarkGray.copy(alpha = 0.8f) // Fondo gris
        )

    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BoxWithCameraIcon()

            Spacer(modifier = Modifier.width(24.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = userName, // Usar el nombre del usuario
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Text(
                    text = userEmail, // Usar el email del usuario
                    color = textColor.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 4.dp)
                )

                val deepBlue = Color(0xFF0F1C3F)
                Button(
                    onClick = { /* Cambiar objetivo */ },
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = deepBlue,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cambiar Foto", fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun BoxWithCameraIcon() {
    Box(
        contentAlignment = Alignment.BottomEnd
    ) {
        Image(
            painter = painterResource(R.drawable.btn_4), // Asegúrate de que esta imagen exista
            contentDescription = "Foto de perfil",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        val deepBlue = Color(0xFF0F1C3F)
        IconButton(
            onClick = { /* Cambiar foto */ },
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(deepBlue, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Cambiar Foto",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
            )
        }
    }
}

@Composable
fun PersonalInfoSection(
    cardColor: Color = MaterialTheme.colorScheme.surface,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    fechaNacimiento: String,
    genero: String,
    estatura: String,
    peso: String,
    actividad: String,
    objetivo: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Resumen",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            InfoRow("Fecha de nacimiento", fechaNacimiento, textColor = textColor)
            val deepBlue = Color(0xFF0F1C3F)
            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = deepBlue.copy(alpha = 0.5f)
            )
            InfoRow("Sexo", genero, textColor = textColor)
            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = deepBlue.copy(alpha = 0.5f)
            )
            InfoRow("Estatura", estatura, textColor = textColor)
            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = deepBlue.copy(alpha = 0.5f)
            )
            InfoRow("Peso", peso, textColor = textColor)
            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = deepBlue.copy(alpha = 0.5f)
            )
            InfoRow("Nivel de Actividad", actividad, textColor = textColor)
            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = deepBlue.copy(alpha = 0.5f)
            )
            InfoRow("Objetivo Principal", objetivo, textColor = textColor)

            Spacer(modifier = Modifier.height(16.dp))
            val navyBlue = Color(0xFF1A2C5C)
            Button(
                onClick = { /* ... */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(deepBlue, navyBlue)
                        ),
                        shape = MaterialTheme.shapes.medium // Puedes usar RoundedCornerShape(12.dp) si prefieres
                    )
            ) {
                Text("Editar", color = Color.White)
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String, textColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.Medium, color = textColor)
        Text(value, color = textColor.copy(alpha = 0.8f))
    }
}