package com.example.proyectoe.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.proyectoe.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import com.example.proyectoe.ui.theme.BackgroundColor

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult // Para el selector de imágenes
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.layout.ContentScale

import coil.compose.rememberAsyncImagePainter // Para cargar la imagen
import androidx.compose.ui.platform.LocalContext // Para obtener el contexto de la app
import java.io.File // manejo de archivos
import java.io.FileOutputStream // Para escribir archivos
import java.io.IOException


import androidx.compose.foundation.border // Para el borde del botón

// Definición de colores a nivel de archivo para que sean accesibles globalmente en este archivo

private val CardColor = Color(0xFF1A2C50)       // Tarjetas azul medio
private val PrimaryColor = Color(0xFFF97316)     // Naranja brillante para botones
private val SecondaryColor = Color(0xFFFFFFFF)   // Blanco para textos
private val ErrorColor = Color(0xFFFF6B6B)       // Rojo suave para errores
private val BorderColor = Color(0xFF3A506B)      // Borde azul grisáceo


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateIntro: ()-> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current


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

    // Para subir la foto
    var profilePhotoUri by remember { mutableStateOf<Uri?>(null) } // URI temporal de la galería
    var photoFileNameToSave by remember { mutableStateOf<String?>(null) }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        profilePhotoUri = uri // Actualiza la URI cuando se selecciona una imagen
    }

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

        item { //para el boton de subir la foto
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally, // Centra el botón
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { pickImageLauncher.launch("image/*") }, // Lanza el selector de imagen
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = if (profilePhotoUri != null) "Cambiar Foto" else "Seleccionar Foto de Perfil",
                        color = Color.White
                    )
                }
                // muestra la foto seleccionada en el registro
                if (profilePhotoUri != null) {
                    val currentPhotoPainter = rememberAsyncImagePainter(model = profilePhotoUri)

                    Image(
                        painter = currentPhotoPainter,
                        contentDescription = "Foto de perfil seleccionada",
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .border(2.dp, PrimaryColor, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }


        item {
            SectionCard(
                title = "Información personal", cardColor = CardColor
            ){
                SimpleTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    label = "Nombre",
                    isError = fieldErrors.contains("name"),
                    textColor = SecondaryColor,
                    borderColor = BorderColor
                )
                Divider(
                    color = BorderColor,
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                SimpleTextField(
                    value = userLastName,
                    onValueChange = { userLastName = it },
                    label = "Apellidos",
                    isError = fieldErrors.contains("lastName"),
                    textColor = SecondaryColor,
                    borderColor = BorderColor
                )
                Divider(
                    color = BorderColor,
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                // === MODIFICACIÓN AQUÍ para los Dropdowns de fecha ===
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp), // Padding a nivel de Row
                    horizontalArrangement = Arrangement.spacedBy(8.dp), // Espacio entre los elementos del Row
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1.5f)) {
                        SimpleDropdown(
                            selectedValue = selectedDay?.toString() ?: "",
                            onValueSelected = { selectedDay = it.toInt() },
                            label = "Día",
                            options = days.map { it.toString() },
                            isError = fieldErrors.contains("day"),
                            textColor = SecondaryColor,
                            borderColor = BorderColor,
                            labelFontSize = 12.sp // Ajusta el tamaño de la fuente para el label
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
                            borderColor = BorderColor,
                            labelFontSize = 12.sp // Ajusta el tamaño de la fuente para el label
                        )
                    }
                    Box(modifier = Modifier.weight(1.5f)) {
                        SimpleDropdown(
                            selectedValue = selectedYear?.toString() ?: "",
                            onValueSelected = { selectedYear = it.toInt() },
                            label = "Año",
                            options = years.map { it.toString() },
                            isError = fieldErrors.contains("year"),
                            textColor = SecondaryColor,
                            borderColor = BorderColor,
                            labelFontSize = 12.sp // Ajusta el tamaño de la fuente para el label
                        )
                    }
                }
                // === FIN MODIFICACIÓN ===
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp), // Padding a nivel de Row
                    horizontalArrangement = Arrangement.spacedBy(8.dp), // Espacio entre los elementos del Row
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    //Divider(color = BorderColor, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))

                    SimpleDropdown(
                        selectedValue = selectedGender ?: "",
                        onValueSelected = { selectedGender = it },
                        label = "Género",
                        options = genders,
                        isError = fieldErrors.contains("gender"),
                        textColor = SecondaryColor,
                        borderColor = BorderColor,

                        )
                }
            }
        }
        // Aqui es doinde se modifica los datos fisicos
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
                //Divider(color = BorderColor, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))


                SimpleTextField(
                    value = userHeight,
                    onValueChange = { userHeight = it },
                    label = "Altura (cm)",
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    isError = fieldErrors.contains("height"),
                    textColor = SecondaryColor,
                    borderColor = BorderColor
                )


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp), // Padding a nivel de Row
                    horizontalArrangement = Arrangement.spacedBy(8.dp), // Espacio entre los elementos del Row
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SimpleDropdown(
                        selectedValue = selectedActivityLevel ?: "",
                        onValueSelected = { selectedActivityLevel = it },
                        label = "Nivel de actividad",
                        options = activityLevels,
                        isError = fieldErrors.contains("activity"),
                        textColor = SecondaryColor,
                        borderColor = BorderColor
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp), // Padding a nivel de Row
                    horizontalArrangement = Arrangement.spacedBy(8.dp), // Espacio entre los elementos del Row
                    verticalAlignment = Alignment.CenterVertically
                ) {

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

                        // 1. Intentar registrar el usuario con Firebase Auth
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnSuccessListener { authResult ->
                                val uid =
                                    authResult.user?.uid // Obtenemos el UID real del nuevo usuario
                                if (uid != null) {
                                    var finalPhotoFileName: String? =
                                        null // <-- ¡Esta variable es clave!

                                    // 2. Si hay una URI de foto seleccionada, intentamos guardarla
                                    if (profilePhotoUri != null) { // <-- ¡Este bloque es clave para guardar la foto!
                                        try {
                                            // Usamos el UID real del usuario para el nombre del archivo
                                            val newFileName =
                                                "profile_photo_${uid}_${System.currentTimeMillis()}.jpg"
                                            val destinationFile =
                                                File(context.filesDir, newFileName)

                                            context.contentResolver.openInputStream(profilePhotoUri!!)
                                                ?.use { inputStream ->
                                                    FileOutputStream(destinationFile).use { outputStream ->
                                                        inputStream.copyTo(outputStream)
                                                    }
                                                }
                                            finalPhotoFileName =
                                                newFileName // Asignamos el nombre del archivo guardado
                                            println("DEBUG: Foto guardada localmente: $finalPhotoFileName") // Debug log
                                        } catch (e: IOException) {
                                            println("ERROR: Fallo al guardar la foto localmente: ${e.message}") // Debug log de error
                                            errorMessage =
                                                "Error al guardar la foto de perfil. Intente de nuevo."
                                            // No se detiene el registro, solo la foto no se guarda
                                        }
                                    } else {
                                        println("DEBUG: No hay foto de perfil seleccionada para guardar.") // Debug log
                                    }

                                    // 3. Guardar los datos del usuario (incluyendo el nombre del archivo de la foto) en Firestore
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
                                        email = email,
                                        photoFileName = finalPhotoFileName
                                    )
                                    db.collection("usuarios").document(uid).set(newUser)
                                        .addOnSuccessListener {
                                            isProcessing = false
                                            println("DEBUG: Datos de usuario guardados en Firestore. photoFileName: $finalPhotoFileName") // Debug log
                                            onRegisterSuccess()
                                        }
                                        .addOnFailureListener { e ->
                                            isProcessing = false
                                            errorMessage =
                                                "Error al guardar datos de usuario en Firestore: ${e.message}"
                                            println("ERROR: Fallo al guardar datos de usuario en Firestore: ${e.message}")
                                        }
                                } else {
                                    isProcessing = false
                                    errorMessage = "Error de registro: UID no disponible."
                                    println("ERROR: UID no disponible después del registro de autenticación.") // Debug log de error
                                }
                                }
                                    .addOnFailureListener { e ->
                                        isProcessing = false
                                        errorMessage = "Error de autenticación: ${e.message}"
                                        println("ERROR: Fallo en autenticación de Firebase: ${e.message}")
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
        item {
            TextButton(
                onClick = onNavigateIntro,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    "Volver",
                    color = SecondaryColor,
                    fontSize = 14.sp
                )
            }
        }
    }
}

// ... (SectionCard, SimpleTextField, PasswordField sin cambios relevantes aquí) ...

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
    borderColor: Color = Color.Gray,
    labelFontSize: androidx.compose.ui.unit.TextUnit = 16.sp // Nuevo parámetro para el tamaño de la fuente del label
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = if (isError) ErrorColor else SecondaryColor, fontSize = labelFontSize) }, // Usa el nuevo parámetro
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
    borderColor: Color = Color.Gray,
    labelFontSize: androidx.compose.ui.unit.TextUnit = 16.sp // Nuevo parámetro para el tamaño de la fuente del label
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = if (isError) ErrorColor else SecondaryColor, fontSize = labelFontSize) }, // Usa el nuevo parámetro
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
    borderColor: Color = Color.Gray,
    labelFontSize: androidx.compose.ui.unit.TextUnit = 14.sp // Nuevo parámetro con un valor por defecto más pequeño
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier.fillMaxWidth() // Se eliminó el padding de aquí para manejarlo en el Row
    ) {
        OutlinedTextField(
            readOnly = true,
            value = selectedValue,
            onValueChange = {},
            label = { Text(label, color = if (isError) ErrorColor else SecondaryColor, fontSize = labelFontSize) }, // Usa el nuevo parámetro
            isError = isError,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
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