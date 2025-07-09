// src/main/java/com/example/proyectoe/ui/auth/RegisterScreen.kt
package com.example.proyectoe.ui.auth
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import com.example.proyectoe.database.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
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

    var error by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { Text("Registro", style = MaterialTheme.typography.titleLarge) }

        item {
            OutlinedTextField(
                value = userName,
                onValueChange = { userName = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = userLastName,
                onValueChange = { userLastName = it },
                label = { Text("Apellidos") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = userWeight,
                onValueChange = { userWeight = it },
                label = { Text("Peso (kg)") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = userHeight,
                onValueChange = { userHeight = it },
                label = { Text("Altura (cm)") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            DropdownField("Día", selectedDay?.toString() ?: "", days.map { it.toString() }) { selectedDay = it.toInt() }
        }

        item {
            DropdownField("Mes", selectedMonth ?: "", months) { selectedMonth = it }
        }

        item {
            DropdownField("Año", selectedYear?.toString() ?: "", years.map { it.toString() }) { selectedYear = it.toInt() }
        }

        item {
            DropdownField("Género", selectedGender ?: "", genders) { selectedGender = it }
        }

        item {
            DropdownField("Nivel de actividad", selectedActivityLevel ?: "", activityLevels) { selectedActivityLevel = it }
        }

        item {
            DropdownField("Objetivo", selectedObjective ?: "", objectives) { selectedObjective = it }
        }

        item {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Button(
                onClick = {
                    if (password != confirmPassword) {
                        error = "Las contraseñas no coinciden"
                        return@Button
                    }

                    if (selectedDay == null || selectedMonth == null || selectedYear == null ||
                        selectedGender == null || selectedActivityLevel == null || selectedObjective == null
                    ) {
                        error = "Completa todos los campos requeridos"
                        return@Button
                    }

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
                                .addOnSuccessListener { onRegisterSuccess() }
                                .addOnFailureListener { e -> error = e.message }
                        }
                        .addOnFailureListener { e ->
                            error = e.message
                        }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar")
            }
        }

        item {
            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }

        item {
            TextButton(onClick = onNavigateBack) {
                Text("¿Ya tienes cuenta? Inicia sesión")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(label: String, selected: String, options: List<String>, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { value ->
                DropdownMenuItem(text = { Text(value) }, onClick = {
                    onSelect(value)
                    expanded = false
                })
            }
        }
    }
}