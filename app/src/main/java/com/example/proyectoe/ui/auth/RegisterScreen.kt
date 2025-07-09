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

// Importaciones necesarias para ExposedDropdownMenuBox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions

// Importar la clase Calendar
import java.util.Calendar // <--- ¡Asegúrate de importar esto!

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()

    // --- User Profile State ---
    var userName by remember { mutableStateOf("") }
    var userLastName by remember { mutableStateOf("") }
    var userHeight by remember { mutableStateOf("") }
    var userWeight by remember { mutableStateOf("") }
    // --- Date of Birth State ---
    // --- CORRECCIÓN AQUÍ ---
    val currentYear = Calendar.getInstance().get(Calendar.YEAR) // <--- ¡Esta es la línea corregida!
    // --- FIN CORRECCIÓN ---
    val years = (currentYear - 100..currentYear).toList().reversed()
    val days = (1..31).toList()
    val months = listOf(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )
    var selectedDay by remember { mutableStateOf<Int?>(null) }
    var selectedMonth by remember { mutableStateOf<String?>(null) }
    var selectedYear by remember { mutableStateOf<Int?>(null) }
    var expandedDay by remember { mutableStateOf(false) }
    var expandedMonth by remember { mutableStateOf(false) }
    var expandedYear by remember { mutableStateOf(false) }

    // --- Dropdown States for other fields ---
    val weights = (40..200).toList()
    var selectedWeight by remember { mutableStateOf<Int?>(null) }
    var expandedWeight by remember { mutableStateOf(false) }

    val genders = listOf("Masculino", "Femenino", "Otro")
    var selectedGender by remember { mutableStateOf<String?>(null) }
    var expandedGender by remember { mutableStateOf(false) }

    val activityLevels = listOf("Nada", "Bajo", "Moderado", "Alto")
    var selectedActivityLevel by remember { mutableStateOf<String?>(null) }
    var expandedActivityLevel by remember { mutableStateOf(false) }

    val objectives = listOf("Perder peso", "Ganar músculo", "Mantener peso", "Mejorar salud")
    var selectedObjective by remember { mutableStateOf<String?>(null) }
    var expandedObjective by remember { mutableStateOf(false) }

    // --- Authentication State ---
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text("Crea tu cuenta de FitTrack", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            OutlinedTextField(
                value = userName,
                onValueChange = { userName = it },
                label = { Text("Nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = userLastName,
                onValueChange = { userLastName = it },
                label = { Text("Apellidos") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Text("Fecha de Nacimiento", style = MaterialTheme.typography.labelLarge)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Selector de Día
                ExposedDropdownMenuBox(
                    expanded = expandedDay,
                    onExpandedChange = { expandedDay = !expandedDay },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = selectedDay?.toString() ?: "Día",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Día") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDay)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedDay,
                        onDismissRequest = { expandedDay = false }
                    ) {
                        days.forEach { day ->
                            DropdownMenuItem(
                                text = { Text(day.toString()) },
                                onClick = {
                                    selectedDay = day
                                    expandedDay = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Selector de Mes
                ExposedDropdownMenuBox(
                    expanded = expandedMonth,
                    onExpandedChange = { expandedMonth = !expandedMonth },
                    modifier = Modifier.weight(1.5f)
                ) {
                    OutlinedTextField(
                        value = selectedMonth ?: "Mes",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Mes") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMonth)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedMonth,
                        onDismissRequest = { expandedMonth = false }
                    ) {
                        months.forEach { month ->
                            DropdownMenuItem(
                                text = { Text(month) },
                                onClick = {
                                    selectedMonth = month
                                    expandedMonth = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Selector de Año
                ExposedDropdownMenuBox(
                    expanded = expandedYear,
                    onExpandedChange = { expandedYear = !expandedYear },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = selectedYear?.toString() ?: "Año",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Año") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedYear)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedYear,
                        onDismissRequest = { expandedYear = false }
                    ) {
                        years.forEach { year ->
                            DropdownMenuItem(
                                text = { Text(year.toString()) },
                                onClick = {
                                    selectedYear = year
                                    expandedYear = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // --- Selector de Peso (Kg) ---
        item {
            OutlinedTextField(
                value = userWeight,
                onValueChange = { userWeight = it },
                label = { Text("Peso (km)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // --- Campo de Altura (manteniendo como TextField) ---
        item {
            OutlinedTextField(
                value = userHeight,
                onValueChange = { userHeight = it },
                label = { Text("Altura (cm)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // --- Selector de Género ---
        item {
            ExposedDropdownMenuBox(
                expanded = expandedGender,
                onExpandedChange = { expandedGender = !expandedGender },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedGender ?: "Selecciona tu género",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Género") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGender) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedGender,
                    onDismissRequest = { expandedGender = false }
                ) {
                    genders.forEach { gender ->
                        DropdownMenuItem(
                            text = { Text(gender) },
                            onClick = {
                                selectedGender = gender
                                expandedGender = false
                            }
                        )
                    }
                }
            }
        }

        // --- Selector de Nivel de Actividad Física ---
        item {
            ExposedDropdownMenuBox(
                expanded = expandedActivityLevel,
                onExpandedChange = { expandedActivityLevel = !expandedActivityLevel },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedActivityLevel ?: "Selecciona tu nivel de actividad",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Nivel de Actividad Física") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedActivityLevel) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedActivityLevel,
                    onDismissRequest = { expandedActivityLevel = false }
                ) {
                    activityLevels.forEach { level ->
                        DropdownMenuItem(
                            text = { Text(level) },
                            onClick = {
                                selectedActivityLevel = level
                                expandedActivityLevel = false
                            }
                        )
                    }
                }
            }
        }

        // --- Selector de Objetivo ---
        item {
            ExposedDropdownMenuBox(
                expanded = expandedObjective,
                onExpandedChange = { expandedObjective = !expandedObjective },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedObjective ?: "Selecciona tu objetivo",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Objetivo") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedObjective) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedObjective,
                    onDismissRequest = { expandedObjective = false }
                ) {
                    objectives.forEach { objective ->
                        DropdownMenuItem(
                            text = { Text(objective) },
                            onClick = {
                                selectedObjective = objective
                                expandedObjective = false
                            }
                        )
                    }
                }
            }
        }

        // --- Authentication Fields ---
        item {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // --- Register Button and Error Message ---
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (password != confirmPassword) {
                        error = "Las contraseñas no coinciden"
                    } else if (selectedDay == null || selectedMonth == null || selectedYear == null ||
                        selectedWeight == null || userHeight.isBlank() || selectedGender == null ||
                        selectedActivityLevel == null || selectedObjective == null
                    ) {
                        error = "Por favor, completa todos los campos."
                    } else {
                        val birthDate = "$selectedDay ${selectedMonth}, $selectedYear"
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnSuccessListener {
                                onRegisterSuccess()
                            }
                            .addOnFailureListener { e ->
                                error = e.message
                            }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar")
            }
        }

        item {
            error?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = onNavigateBack) {
                Text("¿Ya tienes cuenta? Inicia sesión")
            }
        }
    }
}