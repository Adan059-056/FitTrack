package com.example.proyectoe.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import com.example.proyectoe.database.User

// Definición de colores a nivel de archivo para que sean accesibles globalmente en este archivo
private val BackgroundColor = Color(0xFF0F172A) // Fondo azul oscuro
private val CardColor = Color(0xFF1A2C50)       // Tarjetas azul medio
private val PrimaryColor = Color(0xFFF97316)     // Naranja brillante para botones
private val SecondaryColor = Color(0xFFFFFFFF)   // Blanco para textos
private val ErrorColor = Color(0xFFFF6B6B)       // Rojo suave para errores
private val BorderColor = Color(0xFF3A506B)      // Borde azul grisáceo


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    // Estados para los datos del usuario
    var userName by remember { mutableStateOf("") }
    var userLastName by remember { mutableStateOf("") }
    var userHeight by remember { mutableStateOf("") }
    var userWeight by remember { mutableStateOf("") }

    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val days = (1..31).toList()
    val months = listOf("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre")
    val years = (currentYear - 100..currentYear).toList().reversed()

    var selectedDay by remember { mutableStateOf<Int?>(null) }
    var selectedMonth by remember { mutableStateOf<String?>(null) }
    var selectedYear by remember { mutableStateOf<Int?>(null) }

    var selectedGender by remember { mutableStateOf<String?>(null) }
    val genders = listOf("Masculino", "Femenino", "Otro")

    var selectedActivityLevel by remember { mutableStateOf<String?>(null) }
    val activityLevels = listOf("Nada", "Bajo", "Moderado", "Alto")

    var selectedObjective by remember { mutableStateOf<String?>(null) }
    val objectives = listOf("Perder peso", "Ganar músculo", "Mantener peso", "Mejorar salud")

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Estados para errores y UI
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var fieldErrors by remember { mutableStateOf(setOf<String>()) }
    var isProcessing by remember { mutableStateOf(false) }

    fun validateFields(): Boolean {
        val errors = mutableSetOf<String>()

        if (userName.isEmpty()) errors.add("name")
        if (userLastName.isEmpty()) errors.add("lastName")
        if (userWeight.isEmpty()) errors.add("weight")
        if (userHeight.isEmpty()) errors.add("height")

        if (selectedDay == null) errors.add("day")
        if (selectedMonth == null) errors.add("month")
        if (selectedYear == null) errors.add("year")
        if (selectedGender == null) errors.add("gender")
        if (selectedActivityLevel == null) errors.add("activity")
        if (selectedObjective == null) errors.add("objective")


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errors.add("email")
        }

        if (password.length < 6) {
            errors.add("password")
            errorMessage = "La contraseña debe tener al menos 6 caracteres"
        } else if (password != confirmPassword) {
            errors.add("confirmPassword")
            errorMessage = "Las contraseñas no coinciden"
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
                "FitTrack",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)
            )
        }

        item {
            Text(
                "Crea tu cuenta",
                style = MaterialTheme.typography.titleMedium,
                color = SecondaryColor,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        item {
            SectionCard(title = "Información personal", cardColor = CardColor) {
                SimpleTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    label = "Nombre",
                    isError = fieldErrors.contains("name"),
                    textColor = SecondaryColor,
                    borderColor = BorderColor
                )
                Divider(color = BorderColor, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))

                SimpleTextField(
                    value = userLastName,
                    onValueChange = { userLastName = it },
                    label = "Apellidos",
                    isError = fieldErrors.contains("lastName"),
                    textColor = SecondaryColor,
                    borderColor = BorderColor
                )
                Divider(color = BorderColor, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        SimpleDropdown(
                            selectedValue = selectedDay?.toString() ?: "",
                            onValueSelected = { selectedDay = it.toInt() },
                            label = "Día",
                            options = days.map { it.toString() },
                            isError = fieldErrors.contains("day"),
                            textColor = SecondaryColor,
                            borderColor = BorderColor
                        )
                    }
                    Box(modifier = Modifier.weight(1.5f)) {
                        SimpleDropdown(
                            selectedValue = selectedMonth ?: "",
                            onValueSelected = { selectedMonth = it },
                            label = "Mes",
                            options = months,
                            isError = fieldErrors.contains("month"),
                            textColor = SecondaryColor,
                            borderColor = BorderColor
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) { // Corrección aquí: Usar .weight(1f) en lugar de .Weight(1f)
                        SimpleDropdown(
                            selectedValue = selectedYear?.toString() ?: "",
                            onValueSelected = { selectedYear = it.toInt() },
                            label = "Año",
                            options = years.map { it.toString() },
                            isError = fieldErrors.contains("year"),
                            textColor = SecondaryColor,
                            borderColor = BorderColor
                        )
                    }
                }
                Divider(color = BorderColor, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))

                SimpleDropdown(
                    selectedValue = selectedGender ?: "",
                    onValueSelected = { selectedGender = it },
                    label = "Género",
                    options = genders,
                    isError = fieldErrors.contains("gender"),
                    textColor = SecondaryColor,
                    borderColor = BorderColor
                )
            }
        }

        item {
            SectionCard(title = "Datos físicos", cardColor = CardColor) {
                SimpleTextField(
                    value = userWeight,
                    onValueChange = { userWeight = it },
                    label = "Peso (kg)",
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    isError = fieldErrors.contains("weight"),
                    textColor = SecondaryColor,
                    borderColor = BorderColor
                )
                Divider(color = BorderColor, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))

                SimpleTextField(
                    value = userHeight,
                    onValueChange = { userHeight = it },
                    label = "Altura (cm)",
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    isError = fieldErrors.contains("height"),
                    textColor = SecondaryColor,
                    borderColor = BorderColor
                )
                Divider(color = BorderColor, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))

                SimpleDropdown(
                    selectedValue = selectedActivityLevel ?: "",
                    onValueSelected = { selectedActivityLevel = it },
                    label = "Nivel de actividad",
                    options = activityLevels,
                    isError = fieldErrors.contains("activity"),
                    textColor = SecondaryColor,
                    borderColor = BorderColor
                )
                Divider(color = BorderColor, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))

                SimpleDropdown(
                    selectedValue = selectedObjective ?: "",
                    onValueSelected = { selectedObjective = it },
                    label = "Objetivo",
                    options = objectives,
                    isError = fieldErrors.contains("objective"),
                    textColor = SecondaryColor,
                    borderColor = BorderColor
                )
            }
        }

        item {
            SectionCard(title = "Cuenta", cardColor = CardColor) {
                SimpleTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Correo electrónico",
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                    isError = fieldErrors.contains("email"),
                    textColor = SecondaryColor,
                    borderColor = BorderColor
                )
                Divider(color = BorderColor, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))

                PasswordField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Contraseña",
                    isError = fieldErrors.contains("password"),
                    textColor = SecondaryColor,
                    borderColor = BorderColor
                )
                Divider(color = BorderColor, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))

                PasswordField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = "Confirmar Contraseña",
                    isError = fieldErrors.contains("confirmPassword"),
                    textColor = SecondaryColor,
                    borderColor = BorderColor
                )
            }
        }

        item {
            errorMessage?.let {
                Text(
                    text = it,
                    color = ErrorColor,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
        }

        item {
            Button(
                onClick = {
                    if (validateFields()) {
                        isProcessing = true
                        val fechaNacimiento = "${selectedDay} ${selectedMonth}, ${selectedYear}"

                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnSuccessListener { result ->
                                val uid = result.user?.uid ?: ""
                                val newUser = User(
                                    uid = uid,
                                    nombre = userName,
                                    apellidos = userLastName,
                                    fechaNacimiento = fechaNacimiento,
                                    peso = userWeight,
                                    altura = userHeight,
                                    genero = selectedGender ?: "",
                                    actividad = selectedActivityLevel ?: "",
                                    objetivo = selectedObjective ?: "",
                                    email = email
                                )
                                db.collection("usuarios").document(uid).set(newUser)
                                    .addOnSuccessListener {
                                        isProcessing = false
                                        onRegisterSuccess()
                                    }
                                    .addOnFailureListener { e ->
                                        isProcessing = false
                                        errorMessage = e.message
                                    }
                            }
                            .addOnFailureListener { e ->
                                isProcessing = false
                                errorMessage = e.message
                            }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(50.dp),
                shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryColor,
                    disabledContainerColor = PrimaryColor.copy(alpha = 0.5f)
                ),
                enabled = !isProcessing
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text("Registrar", fontSize = 16.sp, color = Color.White)
                }
            }
        }

        item {
            TextButton(onClick = onNavigateBack) {
                Text(
                    "¿Ya tienes cuenta? Inicia sesión",
                    color = SecondaryColor,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun SectionCard(
    title: String,
    cardColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(cardColor)
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title.uppercase(),
            color = SecondaryColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 4.dp)
        )
        content()
    }
}

@Composable
fun SimpleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    textColor: Color = Color.Black,
    borderColor: Color = Color.Gray
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = if (isError) ErrorColor else SecondaryColor) },
        placeholder = { Text(placeholder, color = Color(0xFFA0A0B0)) },
        isError = isError,
        singleLine = true,
        keyboardOptions = keyboardOptions,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedTextColor = textColor,
            unfocusedTextColor = textColor,
            focusedIndicatorColor = if (isError) ErrorColor else borderColor,
            unfocusedIndicatorColor = if (isError) ErrorColor else borderColor,
            cursorColor = textColor
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Black,
    borderColor: Color = Color.Gray
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = if (isError) ErrorColor else SecondaryColor) },
        isError = isError,
        singleLine = true,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedTextColor = textColor,
            unfocusedTextColor = textColor,
            focusedIndicatorColor = if (isError) ErrorColor else borderColor,
            unfocusedIndicatorColor = if (isError) ErrorColor else borderColor,
            cursorColor = textColor
        ),
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                    tint = textColor
                )
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleDropdown(
    selectedValue: String,
    onValueSelected: (String) -> Unit,
    label: String,
    options: List<String>,
    isError: Boolean,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Black,
    borderColor: Color = Color.Gray
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        OutlinedTextField(
            readOnly = true,
            value = selectedValue,
            onValueChange = {},
            label = { Text(label, color = if (isError) ErrorColor else SecondaryColor) },
            isError = isError,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }, // <-- Línea corregida aquí, se eliminó 'tint'
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedTextColor = textColor,
                unfocusedTextColor = textColor,
                focusedIndicatorColor = if (isError) ErrorColor else borderColor,
                unfocusedIndicatorColor = if (isError) ErrorColor else borderColor,
                cursorColor = textColor
            ),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(CardColor)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            color = textColor,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    onClick = {
                        onValueSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}