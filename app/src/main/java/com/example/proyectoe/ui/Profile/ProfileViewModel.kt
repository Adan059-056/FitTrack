package com.example.proyectoe.ui.Profile

import androidx.lifecycle.viewModelScope
import com.example.proyectoe.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import android.net.Uri

import java.io.File
import java.io.FileOutputStream
import java.io.IOException

import android.app.Application
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

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing

    private val _editableUser = MutableStateFlow<User?>(null)
    val editableUser: StateFlow<User?> = _editableUser

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

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
                        _editableUser.value = userData?.copy()

                        val storedPhotoFileName = userData?.photoFileName
                        if (storedPhotoFileName != null && storedPhotoFileName.isNotEmpty()) {
                            val file = File(appContext.filesDir, storedPhotoFileName)
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
            val currentPhotoFileName = _user.value?.photoFileName
            _profilePhotoUri.value = if (currentPhotoFileName != null && currentPhotoFileName.isNotEmpty()) {
                val file = File(appContext.filesDir, currentPhotoFileName)
                if (file.exists()) Uri.fromFile(file) else null
            } else {
                null
            }
        } else {
            _editableUser.value = _user.value?.copy()
            val currentPhotoFileName = _user.value?.photoFileName
            _profilePhotoUri.value = if (currentPhotoFileName != null && currentPhotoFileName.isNotEmpty()) {
                val file = File(appContext.filesDir, currentPhotoFileName)
                if (file.exists()) Uri.fromFile(file) else null
            } else {
                null
            }
        }
    }

    fun updateEditableName(name: String) {
        _editableUser.update { it?.copy(nombre = name) }
    }

    fun updateEditableApellidos(apellidos: String) {
        _editableUser.update { it?.copy(apellidos = apellidos) }
    }

    fun updateEditableFechaNacimiento(fecha: String) {
        _editableUser.update { it?.copy(fechaNacimiento = fecha) }
    }

    fun updateEditablePeso(peso: String) {
        _editableUser.update { it?.copy(peso = peso) }
    }

    fun updateEditableAltura(altura: String) {
        _editableUser.update { it?.copy(altura = altura) }
    }

    fun updateEditableGenero(genero: String) {
        _editableUser.update { it?.copy(genero = genero) }
    }

    fun updateEditableActividad(actividad: String) {
        _editableUser.update { it?.copy(actividad = actividad) }
    }

    fun updateEditableObjetivo(objetivo: String) {
        _editableUser.update { it?.copy(objetivo = objetivo) }
    }

    fun saveProfileAndPhotoChanges() {
        _isLoading.value = true
        _errorMessage.value = null
        _isSuccess.value = null
        viewModelScope.launch {
            val userId = auth.currentUser?.uid
            val userToSave = _editableUser.value
            val currentSelectedPhotoUri = _profilePhotoUri.value

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
                var photoFileNameToSave: String? = userToSave.photoFileName

                if (currentSelectedPhotoUri != null && currentSelectedPhotoUri.scheme == "content") {
                    val newFileName = "profile_photo_${userId}_${System.currentTimeMillis()}.jpg"
                    val destinationFile = File(appContext.filesDir, newFileName)

                    appContext.contentResolver.openInputStream(currentSelectedPhotoUri)?.use { inputStream ->
                        FileOutputStream(destinationFile).use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                    photoFileNameToSave = newFileName
                    _profilePhotoUri.value = Uri.fromFile(destinationFile)
                }

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
                    "photoFileName" to photoFileNameToSave
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