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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.text.selection.TextSelectionColors

import androidx.compose.runtime.*
import androidx.compose.runtime.derivedStateOf

@OptIn(ExperimentalMaterial3Api::class)
//@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditFoodScreen(
    foodItemId: String,
    onBack: () -> Unit = {},
    onFoodUpdated: () -> Unit = {},
    foodViewModel: FoodViewModel = viewModel()
) {
    // Definición de colores para el tema oscuro
    val darkBlueBlack = Color(0xFF0A0E21)
    val orangePrimary = Color(0xFFFF9800)
    val orangeSecondary = Color(0xFFFF5722)
    val darkSurface = Color(0xFF121212)
    val textColor = Color(0xFFE0E0E0)
    val cardColor = Color(0xFF1E1E2D)

    var name by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") } // Nuevo campo: proteínas
    var fat by remember { mutableStateOf("") }
    var carbohydrates by remember { mutableStateOf("") }

    var isLoadingInitialData by remember { mutableStateOf(true) } // Para la carga inicial del alimento
    var errorMessageLocal by remember { mutableStateOf<String?>(null) } // Mensajes específicos de la UI
    var successMessageLocal by remember { mutableStateOf<String?>(null) }

    val isViewModelLoading = foodViewModel.isLoading.collectAsState().value
    val firebaseErrorMessage = foodViewModel.errorMessage.collectAsState().value

    // Cargar datos del alimento
    LaunchedEffect(foodItemId) {
        isLoadingInitialData = true // Indica que estamos cargando
        errorMessageLocal = null // Limpia cualquier mensaje de error anterior
        foodViewModel.getFoodItemById(foodItemId)?.let { foodItem -> // Usar getFoodItemById
            name = foodItem.name
            details = foodItem.details
            calories = foodItem.calories.toString() // Convertir Float a String
            protein = foodItem.protein.toString()   // Convertir Float a String
            fat = foodItem.fat.toString()           // Convertir Float a String
            carbohydrates = foodItem.carbohydrates.toString() // Convertir Float a String
        } ?: run {
            errorMessageLocal = "No se pudo cargar el alimento. ID: $foodItemId"
        }
        isLoadingInitialData = false // La carga inicial ha terminado
    }

    val isFormValid by remember {
        derivedStateOf {
            name.isNotBlank() &&
                    calories.toFloatOrNull() != null && calories.toFloatOrNull()!! >= 0 &&
                    protein.toFloatOrNull() != null && protein.toFloatOrNull()!! >= 0 &&
                    fat.toFloatOrNull() != null && fat.toFloatOrNull()!! >= 0 &&
                    carbohydrates.toFloatOrNull() != null && carbohydrates.toFloatOrNull()!! >= 0 // Validar carbohidratos
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
                if (isLoadingInitialData || isViewModelLoading) { // Mostrar spinner si se está cargando inicialmente o si el ViewModel está ocupado
                    CircularProgressIndicator(color = orangePrimary)
                } else {
                    // Aquí va la Card decorativa
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
                                text = "Edita la información de tu alimento",
                                style = MaterialTheme.typography.titleMedium,
                                color = textColor
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

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
                        colors = TextFieldDefaults.colors( // USAR TextFieldDefaults.colors
                            focusedContainerColor = cardColor, // Usar cardColor para fondo sólido
                            unfocusedContainerColor = cardColor,
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor,
                            focusedLabelColor = orangePrimary,
                            unfocusedLabelColor = textColor.copy(alpha = 0.6f),
                            cursorColor = orangePrimary,
                            focusedIndicatorColor = orangePrimary,
                            unfocusedIndicatorColor = Color(0xFF2A2A3C),
                            selectionColors = TextSelectionColors( // Añadir selección de colores
                                handleColor = orangePrimary,
                                backgroundColor = orangePrimary.copy(alpha = 0.4f)
                            )
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        isError = name.isBlank() && errorMessageLocal != null
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
                        colors = TextFieldDefaults.colors( // USAR TextFieldDefaults.colors
                            focusedContainerColor = cardColor,
                            unfocusedContainerColor = cardColor,
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor,
                            focusedLabelColor = orangePrimary,
                            unfocusedLabelColor = textColor.copy(alpha = 0.6f),
                            cursorColor = orangePrimary,
                            focusedIndicatorColor = orangePrimary,
                            unfocusedIndicatorColor = Color(0xFF2A2A3C),
                            selectionColors = TextSelectionColors(
                                handleColor = orangePrimary,
                                backgroundColor = orangePrimary.copy(alpha = 0.4f)
                            )
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
                        onValueChange = { newValue ->
                            calories = newValue.filter { char -> char.isDigit() || (char == '.' && !calories.contains('.')) }
                        },
                        label = { Text("Calorías (kcal)", color = textColor.copy(alpha = 0.7f)) },
                        leadingIcon = {
                            Text(
                                "🔥",
                                modifier = Modifier.size(24.dp),
                                // Aquí el color no es de un Text Composable directamente, es para el String emoji
                                // Si quieres tintar el emoji, necesitas un font de iconos.
                                // Por ahora, lo dejamos sin tint, o puedes usar un Icon(painterResource(...))
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors( // USAR TextFieldDefaults.colors
                            focusedContainerColor = cardColor,
                            unfocusedContainerColor = cardColor,
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor,
                            focusedLabelColor = orangePrimary,
                            unfocusedLabelColor = textColor.copy(alpha = 0.6f),
                            cursorColor = orangePrimary,
                            focusedIndicatorColor = orangePrimary,
                            unfocusedIndicatorColor = Color(0xFF2A2A3C),
                            selectionColors = TextSelectionColors(
                                handleColor = orangePrimary,
                                backgroundColor = orangePrimary.copy(alpha = 0.4f)
                            )
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        isError = (calories.toFloatOrNull() == null && calories.isNotBlank()) || (calories.isBlank() && errorMessageLocal != null),
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
                        onValueChange = { newValue ->
                            protein = newValue.filter { char -> char.isDigit() || (char == '.' && !protein.contains('.')) }
                        },
                        label = { Text("Proteínas (g)", color = textColor.copy(alpha = 0.7f)) },
                        leadingIcon = {
                            Text(
                                "🥩", // Icono de proteínas
                                modifier = Modifier.size(24.dp),
                                // color = orangePrimary // Esto es para el Composable Text, no para un String emoji directamente
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors( // USAR TextFieldDefaults.colors
                            focusedContainerColor = cardColor,
                            unfocusedContainerColor = cardColor,
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor,
                            focusedLabelColor = orangePrimary,
                            unfocusedLabelColor = textColor.copy(alpha = 0.6f),
                            cursorColor = orangePrimary,
                            focusedIndicatorColor = orangePrimary,
                            unfocusedIndicatorColor = Color(0xFF2A2A3C),
                            selectionColors = TextSelectionColors(
                                handleColor = orangePrimary,
                                backgroundColor = orangePrimary.copy(alpha = 0.4f)
                            )
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        isError = (protein.toFloatOrNull() == null && protein.isNotBlank()) || (protein.isBlank() && errorMessageLocal != null),
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
                        onValueChange = { newValue ->
                            fat = newValue.filter { char -> char.isDigit() || (char == '.' && !fat.contains('.')) }
                        },
                        label = { Text("Grasas (g)", color = textColor.copy(alpha = 0.7f)) },
                        leadingIcon = {
                            Text(
                                "🥑", // Icono de grasas
                                modifier = Modifier.size(24.dp),
                                // color = orangePrimary
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors( // USAR TextFieldDefaults.colors
                            focusedContainerColor = cardColor,
                            unfocusedContainerColor = cardColor,
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor,
                            focusedLabelColor = orangePrimary,
                            unfocusedLabelColor = textColor.copy(alpha = 0.6f),
                            cursorColor = orangePrimary,
                            focusedIndicatorColor = orangePrimary,
                            unfocusedIndicatorColor = Color(0xFF2A2A3C),
                            selectionColors = TextSelectionColors(
                                handleColor = orangePrimary,
                                backgroundColor = orangePrimary.copy(alpha = 0.4f)
                            )
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        isError = (fat.toFloatOrNull() == null && fat.isNotBlank()) || (fat.isBlank() && errorMessageLocal != null),
                        suffix = {
                            Text(
                                "g",
                                color = textColor.copy(alpha = 0.7f)
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Campo Carbohidratos (NUEVO, añadirlo)
                    OutlinedTextField(
                        value = carbohydrates,
                        onValueChange = { newValue ->
                            carbohydrates = newValue.filter { char -> char.isDigit() || (char == '.' && !carbohydrates.contains('.')) }
                        },
                        label = { Text("Carbohidratos (g)", color = textColor.copy(alpha = 0.7f)) },
                        leadingIcon = {
                            Text(
                                "🍚", // Icono de carbohidratos
                                modifier = Modifier.size(24.dp),
                                // color = orangePrimary
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors( // USAR TextFieldDefaults.colors
                            focusedContainerColor = cardColor,
                            unfocusedContainerColor = cardColor,
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor,
                            focusedLabelColor = orangePrimary,
                            unfocusedLabelColor = textColor.copy(alpha = 0.6f),
                            cursorColor = orangePrimary,
                            focusedIndicatorColor = orangePrimary,
                            unfocusedIndicatorColor = Color(0xFF2A2A3C),
                            selectionColors = TextSelectionColors(
                                handleColor = orangePrimary,
                                backgroundColor = orangePrimary.copy(alpha = 0.4f)
                            )
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        isError = (carbohydrates.toFloatOrNull() == null && carbohydrates.isNotBlank()) || (carbohydrates.isBlank() && errorMessageLocal != null),
                        suffix = {
                            Text(
                                "g",
                                color = textColor.copy(alpha = 0.7f)
                            )
                        }
                    )

                    // Mensajes de error/success (combinados)
                    if (errorMessageLocal != null) {
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
                                text = errorMessageLocal ?: "",
                                color = Color(0xFFF44336),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    if (successMessageLocal != null) {
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
                                text = successMessageLocal ?: "",
                                color = Color(0xFF4CAF50),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    if (firebaseErrorMessage != null && errorMessageLocal == null && successMessageLocal == null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Error Firebase",
                                tint = Color(0xFFF44336),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Error Firebase: $firebaseErrorMessage",
                                color = Color(0xFFF44336),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Botón con gradiente naranja
                    Button(
                        onClick = {
                            if (isFormValid) {
                                val updatedFood = FoodItem(
                                    id = foodItemId, // Asegúrate de pasar el ID para la actualización
                                    name = name,
                                    details = details,
                                    calories = calories.toFloatOrNull() ?: 0f, // Convertir a Float
                                    protein = protein.toFloatOrNull() ?: 0f,   // Convertir a Float
                                    fat = fat.toFloatOrNull() ?: 0f,           // Convertir a Float
                                    carbohydrates = carbohydrates.toFloatOrNull() ?: 0f // Convertir a Float
                                )
                                foodViewModel.updateFoodItem( // Usar updateFoodItem
                                    updatedFood,
                                    onSuccess = {
                                        successMessageLocal = "✅ Alimento actualizado exitosamente"
                                        errorMessageLocal = null
                                        onFoodUpdated() // Llama al callback para que FoodScreen sepa que algo cambió
                                    },
                                    onFailure = { errorMsg ->
                                        errorMessageLocal = "❌ Error al actualizar: $errorMsg"
                                        successMessageLocal = null
                                    }
                                )
                            } else {
                                errorMessageLocal = "⚠️ Completa todos los campos requeridos y asegúrate que los valores sean números válidos."
                                successMessageLocal = null
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !isViewModelLoading && !isLoadingInitialData, // Deshabilita el botón si el ViewModel está ocupado o si se está cargando la data inicial
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
                            if (isViewModelLoading) {
                                CircularProgressIndicator(color = Color.White)
                            } else {
                                Text(
                                    text = "GUARDAR CAMBIOS",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                } // Fin del else (si no está cargando inicialmente)

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