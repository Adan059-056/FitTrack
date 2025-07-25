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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectoe.database.FoodItem
import androidx.compose.runtime.derivedStateOf
import com.example.proyectoe.viewmodel.*
import androidx.compose.foundation.text.selection.TextSelectionColors

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFoodScreen(
    foodId: String? = null, // Parámetro para saber si es modo edición
    onBack: () -> Unit = {},
    foodViewModel: FoodViewModel = viewModel()
) {

    var name by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var carbohydrates by remember{ mutableStateOf("") }

    var localMessage by remember { mutableStateOf("") }
    val isLoading = foodViewModel.isLoading.collectAsState().value
    val firebaseErrorMessage = foodViewModel.errorMessage.collectAsState().value
    val isEditing by remember { mutableStateOf(foodId != null) }

    LaunchedEffect(foodId) {
        if (isEditing && foodId != null) {
            val food = foodViewModel.getFoodItemById(foodId)
            food?.let {
                name = it.name
                details = it.details
                calories = it.calories.toString()
                protein = it.protein.toString()
                fat = it.fat.toString()
                carbohydrates = it.carbohydrates.toString()
            }
        }
    }

    // Validación
    val isFormValid by remember {
        derivedStateOf {
            name.isNotBlank() &&
                    calories.toFloatOrNull() != null && calories.toFloatOrNull()!! >= 0 &&
                    protein.toFloatOrNull() != null && protein.toFloatOrNull()!! >= 0 &&
                    fat.toFloatOrNull() != null && fat.toFloatOrNull()!! >= 0 &&
                    carbohydrates.toFloatOrNull() != null && carbohydrates.toFloatOrNull()!! >= 0
        }
    }

    // colores
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
                            text = if (isEditing) "Edita la información de tu alimento" else "Agrega nuevos alimentos\na tu base de datos",
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
                    leadingIcon = { Icon(Icons.Filled.Fastfood, null, tint = orangePrimary) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = cardColor, // Usar cardColor para fondo sólido
                        unfocusedContainerColor = cardColor,
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        focusedLabelColor = orangePrimary,
                        unfocusedLabelColor = textColor.copy(alpha = 0.6f),
                        cursorColor = orangePrimary,
                        focusedIndicatorColor = orangePrimary,
                        unfocusedIndicatorColor = Color(0xFF2A2A3C),
                        //colores de selección
                        selectionColors = TextSelectionColors(
                            handleColor = orangePrimary,
                            backgroundColor = orangePrimary.copy(alpha = 0.4f)
                        )
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    isError = name.isBlank() && localMessage.isNotEmpty()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo Detalles
                OutlinedTextField(
                    value = details,
                    onValueChange = { details = it },
                    label = { Text("Descripción (opcional)", color = textColor.copy(alpha = 0.7f)) },
                    leadingIcon = { Icon(Icons.Filled.Info, null, tint = orangePrimary) },
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
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

                // Campo Calorías
                OutlinedTextField(
                    value = calories,
                    // Permite números y un solo punto decimal
                    onValueChange = { newValue ->
                        calories = newValue.filter { it.isDigit() || (it == '.' && !calories.contains('.')) }
                    },
                    label = { Text("Calorías (kcal)", color = textColor.copy(alpha = 0.7f)) },
                    leadingIcon = { Text("🔥", modifier = Modifier.size(24.dp), color = orangePrimary) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
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
                    isError = (calories.toFloatOrNull() == null && calories.isNotBlank()) || (calories.isBlank() && localMessage.isNotEmpty()),
                    suffix = { Text("kcal", color = textColor.copy(alpha = 0.7f)) }
                )

                Spacer(modifier = Modifier.height(16.dp))
                //proteinas
                OutlinedTextField(
                    value = protein,
                    onValueChange = { newValue ->
                        protein = newValue.filter { it.isDigit() || (it == '.' && !protein.contains('.')) }
                    },
                    label = { Text("Proteínas (g)", color = textColor.copy(alpha = 0.7f)) },
                    leadingIcon = { Text("🥩", modifier = Modifier.size(24.dp), color = orangePrimary) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
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
                    isError = (protein.toFloatOrNull() == null && protein.isNotBlank()) || (protein.isBlank() && localMessage.isNotEmpty()),
                    suffix = { Text("g", color = textColor.copy(alpha = 0.7f)) }
                )

                Spacer(modifier = Modifier.height(16.dp))
                //grasas
                OutlinedTextField(
                    value = fat,
                    onValueChange = { newValue ->
                        fat = newValue.filter { it.isDigit() || (it == '.' && !fat.contains('.')) }
                    },
                    label = { Text("Grasas (g)", color = textColor.copy(alpha = 0.7f)) },
                    leadingIcon = { Text("🥑", modifier = Modifier.size(24.dp), color = orangePrimary) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
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
                    isError = (fat.toFloatOrNull() == null && fat.isNotBlank()) || (fat.isBlank() && localMessage.isNotEmpty()),
                    suffix = { Text("g", color = textColor.copy(alpha = 0.7f)) }
                )

                Spacer(modifier = Modifier.height(12.dp))
                //carbos
                OutlinedTextField(
                    value = carbohydrates,
                    onValueChange = { newValue ->
                        carbohydrates = newValue.filter { it.isDigit() || (it == '.' && !carbohydrates.contains('.')) }
                    },
                    label = { Text("Carbohidratos (g)", color = textColor.copy(alpha = 0.7f)) },
                    leadingIcon = { Text("🍚", modifier = Modifier.size(24.dp), color = orangePrimary) }, // Icono de carbohidratos
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
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
                    isError = (carbohydrates.toFloatOrNull() == null && carbohydrates.isNotBlank()) || (carbohydrates.isBlank() && localMessage.isNotEmpty()),
                    suffix = { Text("g", color = textColor.copy(alpha = 0.7f)) }
                )

                Spacer(modifier = Modifier.height(32.dp))
                //eroor o exito
                if (localMessage.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if ("exitosamente" in localMessage) Icons.Default.Info else Icons.Filled.Close,
                            contentDescription = "Estado",
                            tint = if ("exitosamente" in localMessage) Color(0xFF4CAF50) else Color(0xFFF44336),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = localMessage,
                            color = if ("exitosamente" in localMessage) Color(0xFF4CAF50) else Color(0xFFF44336),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                if (firebaseErrorMessage != null && localMessage.isEmpty()) {
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
                            val alimento = FoodItem(
                                id = foodId ?: "", // Solo pasa el ID si estamos editando
                                name = name,
                                details = details,
                                calories = calories.toFloatOrNull() ?: 0f,
                                protein = protein.toFloatOrNull() ?: 0f,
                                fat = fat.toFloatOrNull() ?: 0f,
                                carbohydrates = carbohydrates.toFloatOrNull() ?: 0f
                            )

                            if (isEditing) {
                                foodViewModel.updateFoodItem(
                                    alimento,
                                    onSuccess = {
                                        localMessage = "Alimento actualizado exitosamente"
                                        onBack()
                                    },
                                    onFailure = { errorMsg ->
                                        localMessage = "Error al actualizar: $errorMsg"
                                    }
                                )
                            } else {
                                foodViewModel.addFood(
                                    alimento,
                                    onSuccess = {
                                        localMessage = "Alimento agregado exitosamente"
                                        name = ""
                                        details = ""
                                        calories = ""
                                        protein = ""
                                        fat = ""
                                        carbohydrates = ""
                                    },
                                    onFailure = { errorMsg ->
                                        localMessage = "Error al guardar: $errorMsg"
                                    }
                                )
                            }
                        } else {
                            localMessage = "⚠Completa todos los campos requeridos y asegúrate que los valores sean números válidos."
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !isLoading && isFormValid, // Deshabilita el botón si isLoading o el formulario no es válido
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
                                text = if (isEditing) "GUARDAR CAMBIOS" else "GUARDAR ALIMENTO",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }
                    }
                }

                Text(
                    text = "Asegúrate de que los campos numéricos sean válidos.",
                    style = MaterialTheme.typography.bodySmall,
                    color = textColor.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}