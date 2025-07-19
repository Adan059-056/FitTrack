package com.example.proyectoe.ui.Food

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectoe.database.FoodItem
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditFoodScreen(
    foodItemId: String,
    onBack: () -> Unit = {},
    onFoodUpdated: () -> Unit = {}
) {
    // Definición de colores para el tema oscuro
    val darkBlueBlack = Color(0xFF0A0E21)
    val orangePrimary = Color(0xFFFF9800)
    val orangeSecondary = Color(0xFFFF5722)
    val darkSurface = Color(0xFF121212)
    val textColor = Color(0xFFE0E0E0)
    val cardColor = Color(0xFF1E1E2D)

    val db = FirebaseFirestore.getInstance()
    var name by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") } // Nuevo campo: proteínas
    var fat by remember { mutableStateOf("") }     // Nuevo campo: grasas
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    // Cargar datos del alimento
    LaunchedEffect(foodItemId) {
        db.collection("alimentos").document(foodItemId).get()
            .addOnSuccessListener { document ->
                document.toObject(FoodItem::class.java)?.let {
                    name = it.name
                    details = it.details
                    calories = it.calories
                }
                isLoading = false
            }
            .addOnFailureListener {
                errorMessage = "No se pudo cargar el alimento"
                isLoading = false
            }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Editar Alimento",
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Regresar",
                            tint = textColor
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
                if (isLoading) {
                    CircularProgressIndicator(color = orangePrimary)
                } else {
                    // Campo Nombre
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre del alimento", color = textColor.copy(alpha = 0.7f)) },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Fastfood,
                                contentDescription = null,
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
                        modifier = Modifier.fillMaxWidth()
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
                                contentDescription = null,
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

                    // Sección de información nutricional
                    Text(
                        text = "Información Nutricional",
                        style = MaterialTheme.typography.titleMedium,
                        color = orangePrimary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )

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
                        suffix = {
                            Text(
                                "kcal",
                                color = textColor.copy(alpha = 0.7f)
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Campo Proteínas
                    OutlinedTextField(
                        value = protein,
                        onValueChange = { if (it.all { char -> char.isDigit() }) protein = it },
                        label = { Text("Proteínas (g)", color = textColor.copy(alpha = 0.7f)) },
                        leadingIcon = {
                            Text(
                                "🥩", // Icono de proteínas
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
                        suffix = {
                            Text(
                                "g",
                                color = textColor.copy(alpha = 0.7f)
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Campo Grasas
                    OutlinedTextField(
                        value = fat,
                        onValueChange = { if (it.all { char -> char.isDigit() }) fat = it },
                        label = { Text("Grasas (g)", color = textColor.copy(alpha = 0.7f)) },
                        leadingIcon = {
                            Text(
                                "🥑", // Icono de grasas
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
                        suffix = {
                            Text(
                                "g",
                                color = textColor.copy(alpha = 0.7f)
                            )
                        }
                    )

                    // Mensajes de error/success
                    if (errorMessage != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = "Error",
                                tint = Color(0xFFF44336),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = errorMessage ?: "",
                                color = Color(0xFFF44336),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    if (successMessage != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Filled.Info,
                                contentDescription = "Éxito",
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = successMessage ?: "",
                                color = Color(0xFF4CAF50),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Botón con gradiente naranja
                    Button(
                        onClick = {
                            val updatedFood = mapOf(
                                "name" to name,
                                "details" to details,
                                "calories" to calories,
                                "protein" to protein, // Nuevo campo
                                "fat" to fat          // Nuevo campo
                            )

                            db.collection("alimentos").document(foodItemId)
                                .update(updatedFood)
                                .addOnSuccessListener {
                                    successMessage = "✅ Alimento actualizado exitosamente"
                                    errorMessage = null
                                    onFoodUpdated()
                                }
                                .addOnFailureListener {
                                    errorMessage = "❌ Error al actualizar: ${it.message}"
                                    successMessage = null
                                }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
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
                            Text(
                                text = "GUARDAR CAMBIOS",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón para volver
                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    border = BorderStroke(1.dp, textColor.copy(alpha = 0.3f))
                ) {
                    Text("Volver", color = textColor)
                }
            }
        }
    }
}