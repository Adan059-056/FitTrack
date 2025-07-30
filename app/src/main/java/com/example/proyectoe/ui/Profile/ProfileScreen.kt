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
import androidx.compose.material3.CircularProgressIndicator // indicador de carga
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
import androidx.compose.runtime.collectAsState //observar StateFlow
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.viewmodel.compose.viewModel // para obtener la ViewModel
import com.example.proyectoe.R
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext // Para obtener el Context

import coil.compose.rememberAsyncImagePainter

import android.os.Build // Para Build.VERSION.SDK_INT
import androidx.core.content.ContextCompat

import androidx.compose.material3.TextFieldDefaults


import androidx.lifecycle.ViewModelProvider
import android.app.Application
import androidx.lifecycle.viewmodel.CreationExtras


import androidx.lifecycle.ViewModel // <-- ¡ASEGÚRATE DE QUE ESTA ESTÉ!

private val SecondaryColor = Color(0xFFFFFFFF)   // Blanco para textos

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = run {
        val context = LocalContext.current // Obtenemos el Context Composable aquí
        val application = context.applicationContext as Application // Obtenemos el Application context aquí

        viewModel(
            factory = object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                    if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                        @Suppress("UNCHECKED_CAST")
                        return ProfileViewModel(application) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        )
    },
    onBack: () -> Unit = {},
    onLogout: () -> Unit = {}
) {

    val user by viewModel.user.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isSuccess by viewModel.isSuccess.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isEditing by viewModel.isEditing.collectAsState()
    val editableUser by viewModel.editableUser.collectAsState()
    val profilePhotoUri by viewModel.profilePhotoUri.collectAsState()
    val context = LocalContext.current

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.updateProfilePhotoUri(uri) // Actualiza la foto en la ViewModel
            // viewModel.uploadProfilePhotoAndSaveProfileChanges() //para guardar automaticamente al seleccionar la foto
        }
    }

    //permiso para pedir la foto o mas bien abrir la galeria
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            pickImageLauncher.launch("image/*")
        } else {
            Toast.makeText(context, "Permiso para acceder a la galería denegado.", Toast.LENGTH_SHORT).show()
        }
    }

    //colores
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
                    // icono de lapiz pra editrar
                    IconButton(onClick = { viewModel.toggleEditMode() }) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = if (isEditing) "Salir de edición" else "Editar Perfil",
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
                .padding(innerPadding)
        ) {
            when {
                isLoading -> {
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
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Error: $errorMessage", color = Color.Red, fontSize = 18.sp)
                        //botón para reintentar
                        Button(onClick = { viewModel.loadUserProfile() }) {
                            Text("Reintentar")
                        }
                    }
                }
                user != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        if (isSuccess != null) {
                            Text(
                                text = isSuccess!!,
                                color = Color.Green,
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.Black.copy(alpha = 0.5f))
                                    .padding(8.dp)
                                    .align(Alignment.CenterHorizontally)
                            )
                            LaunchedEffect(isSuccess) {
                                delay(3000)
                                viewModel.clearSuccessMessage()
                            }
                            LaunchedEffect(Unit) {
                                viewModel.loadUserProfile()
                            }
                        }

                        ProfileHeader(
                            cardColor = navyBlue.copy(alpha = 0.8f),
                            textColor = Color.White,
                            userName = if (isEditing) editableUser?.nombre ?: "" else "${user?.nombre} ${user?.apellidos}",
                            userEmail = if (isEditing) editableUser?.email ?: "" else user?.email ?: "N/A",
                            isEditing = isEditing,
                            onNameChanged = { viewModel.updateEditableName(it) },
                            onEmailChanged = { /* viewModel.updateEditableEmail(it) */ },
                            onCameraClick = {
                                when {
                                    ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.READ_MEDIA_IMAGES
                                    ) == PackageManager.PERMISSION_GRANTED -> {
                                        pickImageLauncher.launch("image/*")
                                    }
                                    ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.READ_EXTERNAL_STORAGE
                                    ) == PackageManager.PERMISSION_GRANTED -> {
                                        pickImageLauncher.launch("image/*")
                                    }
                                    else -> {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                            requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                                        } else {
                                            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                                        }
                                    }
                                }
                            },
                            profilePhotoUri = profilePhotoUri
                        )


                        PersonalInfoSection(
                            cardColor = navyBlue.copy(alpha = 0.8f),
                            textColor = Color.White,
                            fechaNacimiento = editableUser?.fechaNacimiento ?: "N/A",
                            genero = editableUser?.genero ?: "N/A",
                            estatura = editableUser?.altura ?: "N/A",
                            peso = editableUser?.peso ?: "N/A",
                            actividad = editableUser?.actividad ?: "N/A",
                            objetivo = editableUser?.objetivo ?: "N/A",
                            apellidos = editableUser?.apellidos ?: "N/A",
                            isEditing = isEditing,
                            onFechaNacimientoChanged = { viewModel.updateEditableFechaNacimiento(it) },
                            onGeneroChanged = { viewModel.updateEditableGenero(it) },
                            onEstaturaChanged = { viewModel.updateEditableAltura(it) },
                            onPesoChanged = { viewModel.updateEditablePeso(it) },
                            onActividadChanged = { viewModel.updateEditableActividad(it) },
                            onObjetivoChanged = { viewModel.updateEditableObjetivo(it) },
                            onApellidosChanged = { viewModel.updateEditableApellidos(it) }
                        )
                        if (isEditing) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Button(
                                    onClick = { viewModel.saveProfileAndPhotoChanges() },
                                    colors = ButtonDefaults.buttonColors(containerColor = deepBlue),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Guardar Cambios", color = Color.White)
                                }
                                Spacer(Modifier.width(16.dp))
                                Button(
                                    onClick = { viewModel.toggleEditMode() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Cancelar", color = Color.White)
                                }
                            }
                        }
                    }
                }
                else -> {
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
    userName: String,
    userEmail: String,
    isEditing: Boolean,
    onNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onCameraClick: () -> Unit,
    profilePhotoUri: Uri?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.DarkGray.copy(alpha = 0.8f)
        )

    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BoxWithCameraIcon(
                onCameraClick = onCameraClick,
                profilePhotoUri = profilePhotoUri,
                isEditing = isEditing
            )

            Spacer(modifier = Modifier.width(24.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                if (isEditing) {
                    OutlinedTextField(
                        value = userName,
                        onValueChange = onNameChanged,
                        label = { Text("Nombre Completo") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.Red,
                            unfocusedTextColor = Color.Blue
                        )
                    )
                } else {
                    Text(
                        text = userName,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }

                Spacer(modifier = Modifier.height(4.dp)) // Espacio para el email

                Text(
                    text = userEmail,
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
                    Text("Cambiar Objetivo", fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun BoxWithCameraIcon(
    // Recibe el controlador para lanzar la galería
    onCameraClick: () -> Unit, //Callback para el click en el icono de cámara
    profilePhotoUri: Uri?,
    isEditing: Boolean
) {
    Box(
        contentAlignment = Alignment.BottomEnd
    ) {
        // Carga la imagen
        val painter = if (profilePhotoUri != null) {
            rememberAsyncImagePainter(model = profilePhotoUri)
        } else {
            painterResource(R.drawable.btn_4)
        }

        Image(
            painter = painter,
            contentDescription = "Foto de perfil",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        val deepBlue = Color(0xFF0F1C3F)
        if(isEditing){
        IconButton(
            onClick = onCameraClick,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(deepBlue, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Cambiar foto",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
            )
        }
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
    objetivo: String,
    apellidos: String,
    isEditing: Boolean,
    onFechaNacimientoChanged: (String) -> Unit, // Callbacks para cada campos
    onGeneroChanged: (String) -> Unit,
    onEstaturaChanged: (String) -> Unit,
    onPesoChanged: (String) -> Unit,
    onActividadChanged: (String) -> Unit,
    onObjetivoChanged: (String) -> Unit,
    onApellidosChanged: (String) -> Unit
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
//ss
            if (isEditing) {
                OutlinedTextField(
                    value = apellidos,
                    onValueChange = onApellidosChanged,
                    label = { Text("Apellidos") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),

                    //AQUI ES DONDE se cambia el color de texto cuando se edita el textField

                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.Red,
                        unfocusedTextColor = Color.Blue
                    )


                )

            }


            InfoRowEditable(
                label = "Fecha de nacimiento",
                value = fechaNacimiento,
                textColor = textColor,
                isEditing = isEditing,
                onValueChange = onFechaNacimientoChanged
            )
            val deepBlue = Color(0xFF0F1C3F)
            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = deepBlue.copy(alpha = 0.5f)
            )
            InfoRowEditable(
                label = "Sexo",
                value = genero,
                textColor = textColor,
                isEditing = isEditing,
                onValueChange = onGeneroChanged
            )
            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = deepBlue.copy(alpha = 0.5f)
            )
            InfoRowEditable(
                label = "Estatura",
                value = estatura,
                textColor = textColor,
                isEditing = isEditing,
                onValueChange = onEstaturaChanged,
                suffix = " cm"
            )
            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = deepBlue.copy(alpha = 0.5f)
            )
            InfoRowEditable(
                label = "Peso",
                value = peso,
                textColor = textColor,
                isEditing = isEditing,
                onValueChange = onPesoChanged,
                suffix = " kg"
            )
            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = deepBlue.copy(alpha = 0.5f)
            )
            InfoRowEditable(
                label = "Nivel de Actividad",
                value = actividad,
                textColor = textColor,
                isEditing = isEditing,
                onValueChange = onActividadChanged
            )
            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = deepBlue.copy(alpha = 0.5f)
            )
            InfoRowEditable(
                label = "Objetivo Principal",
                value = objetivo,
                textColor = textColor,
                isEditing = isEditing,
                onValueChange = onObjetivoChanged
            )

            Spacer(modifier = Modifier.height(16.dp))

        }
    }
}

// Composable para manejar el modo de visualización/edición de una fila de información
@Composable
fun InfoRowEditable(
    label: String,
    value: String,
    textColor: Color,
    isEditing: Boolean,
    onValueChange: (String) -> Unit,
    suffix: String = ""
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontWeight = FontWeight.Medium, color = textColor)
        if (isEditing) {
            OutlinedTextField(
                value = value.removeSuffix(suffix),
                onValueChange = { newValue -> onValueChange(newValue) },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Red,
                    unfocusedTextColor = Color.Blue
                ),
                trailingIcon = if (suffix.isNotBlank()) {
                    { Text(suffix, color = textColor.copy(alpha = 0.8f)) }
                } else null
            )

        } else {
            Text(value, color = textColor.copy(alpha = 0.8f))
        }
    }
}