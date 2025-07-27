package com.example.proyectoe.ui.Profile

import androidx.lifecycle.viewModelScope
import com.example.proyectoe.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import android.net.Uri

import java.io.File
import java.io.FileOutputStream
import java.io.IOException

import android.app.Application // <-- ¡NUEVA IMPORTACIÓN!
import androidx.lifecycle.AndroidViewModel

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isSuccess = MutableStateFlow<String?>(null)
    val isSuccess: StateFlow<String?> = _isSuccess

    private val _profilePhotoUri = MutableStateFlow<Uri?>(null)
    val profilePhotoUri: StateFlow<Uri?> = _profilePhotoUri

    private val appContext = application.applicationContext

    fun clearSuccessMessage() {
        _isSuccess.value = null
    }

    //Estado para el modo de edición
    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing

    //Estado para los datos que se están editando (copia de _user)
    private val _editableUser = MutableStateFlow<User?>(null)
    val editableUser: StateFlow<User?> = _editableUser

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    //private val storage = FirebaseStorage.getInstance()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        _isLoading.value = true
        _errorMessage.value = null
        _isSuccess.value = null

        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    val documentSnapshot = db.collection("usuarios").document(userId).get().await()
                    if (documentSnapshot.exists()) {
                        val userData = documentSnapshot.toObject(User::class.java)
                        _user.value = userData
                        _editableUser.value = userData?.copy() // Inicializa editableUser con una copia de los datos actuales

//                        val storedPhotoUrl = userData?.photoUrl
//                        if (storedPhotoUrl != null && storedPhotoUrl.isNotEmpty()) {
//                            _profilePhotoUri.value = Uri.parse(storedPhotoUrl)
//                        } else {
//                            _profilePhotoUri.value = null // Asegurar que sea nulo si no hay URL
//                        }
                        val storedPhotoFileName = userData?.photoFileName
                        if (storedPhotoFileName != null && storedPhotoFileName.isNotEmpty()) {
                            val file = File(appContext.filesDir, storedPhotoFileName) // <-- Usa appContext
                            if (file.exists()) {
                                _profilePhotoUri.value = Uri.fromFile(file)
                            } else {
                                _profilePhotoUri.value = null
                            }
                        } else {
                            _profilePhotoUri.value = null
                        }
                    } else {
                        _errorMessage.value = "No se encontraron datos para el usuario."
                        _user.value = null
                        _editableUser.value = null
                        _profilePhotoUri.value = null
                    }
                } else {
                    _errorMessage.value = "Usuario no autenticado."
                    _user.value = null
                    _editableUser.value = null
                    _profilePhotoUri.value = null
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar el perfil: ${e.message}"
                println("Error al cargar el perfil: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun toggleEditMode() {
        _isEditing.value = !_isEditing.value
        _errorMessage.value = null
        _isSuccess.value = null

        if (_isEditing.value) {
            _editableUser.value = _user.value?.copy()
            // Al entrar en modo edición, reinicia la URI local a la foto del usuario
            val currentPhotoFileName = _user.value?.photoFileName
            _profilePhotoUri.value = if (currentPhotoFileName != null && currentPhotoFileName.isNotEmpty()) {
                val file = File(appContext.filesDir, currentPhotoFileName) // <-- Usa appContext
                if (file.exists()) Uri.fromFile(file) else null
            } else {
                null
            }
        } else {
            _editableUser.value = _user.value?.copy()
            // Al salir del modo edición sin guardar, vuelve a la foto guardada
            val currentPhotoFileName = _user.value?.photoFileName
            _profilePhotoUri.value = if (currentPhotoFileName != null && currentPhotoFileName.isNotEmpty()) {
                val file = File(appContext.filesDir, currentPhotoFileName) // <-- Usa appContext
                if (file.exists()) Uri.fromFile(file) else null
            } else {
                null
            }
        }
    }

    //Funciones para actualizar campos individuales en editableUser
    fun updateEditableName(name: String) {
        _editableUser.value = _editableUser.value?.copy(nombre = name)
    }

    fun updateEditableApellidos(apellidos: String) {
        _editableUser.value = _editableUser.value?.copy(apellidos = apellidos)
    }

    fun updateEditableFechaNacimiento(fecha: String) {
        _editableUser.value = _editableUser.value?.copy(fechaNacimiento = fecha)
    }

    fun updateEditablePeso(peso: String) {
        _editableUser.value = _editableUser.value?.copy(peso = peso)
    }

    fun updateEditableAltura(altura: String) {
        _editableUser.value = _editableUser.value?.copy(altura = altura)
    }

    fun updateEditableGenero(genero: String) {
        _editableUser.value = _editableUser.value?.copy(genero = genero)
    }

    fun updateEditableActividad(actividad: String) {
        _editableUser.value = _editableUser.value?.copy(actividad = actividad)
    }

    fun updateEditableObjetivo(objetivo: String) {
        _editableUser.value = _editableUser.value?.copy(objetivo = objetivo)
    }

    //Función para guardar los cambios en Firestore
    fun saveProfileAndPhotoChanges() {
        _isLoading.value = true
        _errorMessage.value = null
        _isSuccess.value = null
        viewModelScope.launch {
            val userId = auth.currentUser?.uid
            val userToSave = _editableUser.value
            val currentSelectedPhotoUri = _profilePhotoUri.value // Esta es la URI TEMPORAL de la galería

            if (userId == null) {
                _errorMessage.value = "Usuario no autenticado."
                _isLoading.value = false
                return@launch
            }
            if (userToSave == null) {
                _errorMessage.value = "No hay datos de usuario para guardar."
                _isLoading.value = false
                return@launch
            }

            try {
                var photoFileNameToSave: String? = userToSave.photoFileName // Nombre del archivo actual

                // Si se ha seleccionado una nueva URI (de la galería) y no es ya un archivo local
                // (currentSelectedPhotoUri.scheme == "content" indica una URI de la galería)
                if (currentSelectedPhotoUri != null && currentSelectedPhotoUri.scheme == "content") {
                    val newFileName = "profile_photo_${userId}_${System.currentTimeMillis()}.jpg"
                    val destinationFile = File(appContext.filesDir, newFileName) // <-- Usa appContext

                    appContext.contentResolver.openInputStream(currentSelectedPhotoUri)?.use { inputStream -> // <-- Usa appContext
                        FileOutputStream(destinationFile).use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                    photoFileNameToSave = newFileName
                    _profilePhotoUri.value = Uri.fromFile(destinationFile)
                }

                // Prepara las actualizaciones para Firestore
                val updates: Map<String, Any?> = mapOf(
                    "nombre" to userToSave.nombre,
                    "apellidos" to userToSave.apellidos,
                    "fechaNacimiento" to userToSave.fechaNacimiento,
                    "peso" to userToSave.peso,
                    "altura" to userToSave.altura,
                    "genero" to userToSave.genero,
                    "actividad" to userToSave.actividad,
                    "objetivo" to userToSave.objetivo,
                    "email" to userToSave.email,
                    "photoFileName" to photoFileNameToSave // Guarda el nombre del archivo local
                )

                db.collection("usuarios").document(userId).update(updates).await()

                _user.value = userToSave.copy(photoFileName = photoFileNameToSave)
                _isEditing.value = false
                _isSuccess.value = "Perfil actualizado con éxito (localmente)."

            } catch (e: IOException) {
                _errorMessage.value = "Error al guardar la foto localmente: ${e.message}"
                println("Error al guardar la foto localmente: ${e.message}")
            } catch (e: Exception) {
                _errorMessage.value = "Error al guardar los cambios en Firestore: ${e.message}"
                println("Error al guardar los cambios en Firestore: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun updateProfilePhotoUri(uri: Uri?) {
        _profilePhotoUri.value = uri
    }
}