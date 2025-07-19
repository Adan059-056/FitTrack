package com.example.proyectoe.ui.Food

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFoodScreen(onBack: () -> Unit = {}) {
    var name by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var protein by remember { mutableStateOf("") } // Nuevo campo
    var fat by remember { mutableStateOf("") }

    // Validación
    val isFormValid by derivedStateOf {
        name.isNotBlank() &&
                calories.isNotBlank() && calories.toIntOrNull() != null &&
                protein.isNotBlank() && protein.toIntOrNull() != null &&
                fat.isNotBlank() && fat.toIntOrNull() != null
    }

    // Definición de colores según tu tema
    val darkBlueBlack = Color(0xFF0A0E21)
    val orangePrimary = Color(0xFFFF9800)
    val orangeSecondary = Color(0xFFFF5722)
    val darkSurface = Color(0xFF121212)
    val textColor = Color(0xFFE0E0E0)
    val cardColor = Color(0xFF1E1E2D)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Agregar Alimento",
                        fontWeight = FontWeight.Bold,
                        color = orangePrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = orangePrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = darkBlueBlack
                )
            )
        },
        containerColor = darkBlueBlack
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Card decorativa
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = cardColor
                    ),
                    border = BorderStroke(1.dp, Color(0xFF2A2A3C))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Fastfood,
                            contentDescription = "Comida",
                            tint = orangePrimary,
                            modifier = Modifier.size(40.dp)
                        )

                        Spacer(Modifier.width(16.dp))

                        Text(
                            text = "Agrega nuevos alimentos\na tu base de datos",
                            style = MaterialTheme.typography.titleMedium,
                            color = textColor
                        )
                    }
                }

                // Campo Nombre
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del alimento", color = textColor.copy(alpha = 0.7f)) },
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Fastfood,
                            null,
                            tint = orangePrimary
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = darkSurface,
                        unfocusedContainerColor = darkSurface,
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        focusedLabelColor = orangePrimary,
                        unfocusedLabelColor = textColor.copy(alpha = 0.6f),
                        cursorColor = orangePrimary,
                        focusedIndicatorColor = orangePrimary,
                        unfocusedIndicatorColor = Color(0xFF2A2A3C)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    isError = name.isBlank() && mensaje.isNotEmpty()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo Detalles
                OutlinedTextField(
                    value = details,
                    onValueChange = { details = it },
                    label = { Text("Descripción (opcional)", color = textColor.copy(alpha = 0.7f)) },
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Info,
                            null,
                            tint = orangePrimary
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = darkSurface,
                        unfocusedContainerColor = darkSurface,
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        focusedLabelColor = orangePrimary,
                        unfocusedLabelColor = textColor.copy(alpha = 0.6f),
                        cursorColor = orangePrimary,
                        focusedIndicatorColor = orangePrimary,
                        unfocusedIndicatorColor = Color(0xFF2A2A3C)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo Calorías
                OutlinedTextField(
                    value = calories,
                    onValueChange = { if (it.all { char -> char.isDigit() }) calories = it },
                    label = { Text("Calorías (kcal)", color = textColor.copy(alpha = 0.7f)) },
                    leadingIcon = {
                        Text(
                            "🔥",
                            modifier = Modifier.size(24.dp),
                            color = orangePrimary
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = darkSurface,
                        unfocusedContainerColor = darkSurface,
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        focusedLabelColor = orangePrimary,
                        unfocusedLabelColor = textColor.copy(alpha = 0.6f),
                        cursorColor = orangePrimary,
                        focusedIndicatorColor = orangePrimary,
                        unfocusedIndicatorColor = Color(0xFF2A2A3C)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    isError = calories.isBlank() && mensaje.isNotEmpty(),
                    suffix = {
                        Text(
                            "kcal",
                            color = textColor.copy(alpha = 0.7f)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = protein,
                    onValueChange = { if (it.all { char -> char.isDigit() }) protein = it },
                    label = { Text("Proteínas (g)", color = textColor.copy(alpha = 0.7f)) },
                    leadingIcon =
                        {
                        Text(
                            "🥩",
                            modifier = Modifier.size(24.dp),
                            color = orangePrimary
                        )
                        },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = darkSurface,
                        unfocusedContainerColor = darkSurface,
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        focusedLabelColor = orangePrimary,
                        unfocusedLabelColor = textColor.copy(alpha = 0.6f),
                        cursorColor = orangePrimary,
                        focusedIndicatorColor = orangePrimary,
                        unfocusedIndicatorColor = Color(0xFF2A2A3C)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    isError = calories.isBlank() && mensaje.isNotEmpty(),
                    suffix = {
                        Text(
                            "g",
                            color = textColor.copy(alpha = 0.7f)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Mensaje de validación
                if (mensaje.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Error",
                            tint = if ("exitosamente" in mensaje) Color(0xFF4CAF50) else Color(0xFFF44336),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = mensaje,
                            color = if ("exitosamente" in mensaje) Color(0xFF4CAF50) else Color(0xFFF44336),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(5.dp))

                OutlinedTextField(
                    value = fat,
                    onValueChange = { if (it.all { char -> char.isDigit() }) fat = it },
                    label = { Text("Grasas (g)", color = textColor.copy(alpha = 0.7f)) },
                    leadingIcon =
                        {
                            Text(
                                "🥑",
                                modifier = Modifier.size(24.dp),
                                color = orangePrimary
                                )
                        },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = darkSurface,
                        unfocusedContainerColor = darkSurface,
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        focusedLabelColor = orangePrimary,
                        unfocusedLabelColor = textColor.copy(alpha = 0.6f),
                        cursorColor = orangePrimary,
                        focusedIndicatorColor = orangePrimary,
                        unfocusedIndicatorColor = Color(0xFF2A2A3C)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    isError = calories.isBlank() && mensaje.isNotEmpty(),
                    suffix = {
                        Text(
                            "g",
                            color = textColor.copy(alpha = 0.7f)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Botón con gradiente naranja
                Button(
                    onClick = {
                        if (isFormValid) {
                            isLoading = true
                            val db = FirebaseFirestore.getInstance()
                            val alimento = hashMapOf(
                                "name" to name,
                                "details" to details,
                                "calories" to calories,
                                "protein" to protein,
                                "fat" to fat
                            )

                            db.collection("alimentos")
                                .add(alimento)
                                .addOnSuccessListener {
                                    mensaje = "✅ Alimento agregado exitosamente"
                                    name = ""
                                    details = ""
                                    calories = ""
                                    isLoading = false
                                }
                                .addOnFailureListener {
                                    mensaje = "❌ Error al guardar: ${it.message}"
                                    isLoading = false
                                }
                        } else {
                            mensaje = "⚠️ Completa todos los campos requeridos"
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        orangePrimary,
                                        orangeSecondary
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White)
                        } else {
                            Text(
                                text = "GUARDAR ALIMENTO",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }
                    }
                }

                // Texto informativo
                Text(
                    text = "Los campos con * son obligatorios",
                    style = MaterialTheme.typography.bodySmall,
                    color = textColor.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}