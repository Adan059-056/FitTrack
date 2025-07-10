package com.example.proyectoe.ui.Profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoe.database.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileViewModel : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isSuccess = MutableStateFlow<String?>(null)
    val isSuccess: StateFlow<String?> = _isSuccess

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
                    } else {
                        _errorMessage.value = "No se encontraron datos para el usuario."
                        _user.value = null
                        _editableUser.value = null
                    }
                } else {
                    _errorMessage.value = "Usuario no autenticado."
                    _user.value = null
                    _editableUser.value = null
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar el perfil: ${e.message}"
                println("Error al cargar el perfil: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    //Función para cambiar el modo de edición
    fun toggleEditMode() {
        _isEditing.value = !_isEditing.value
        _errorMessage.value = null
        _isSuccess.value = null
        // Cuando entramos en modo edición, hacemos una copia limpia del usuario actual para editar
        // Cuando salimos de modo edición sin guardar, descartamos los cambios pendientes
        if (_isEditing.value) {
            _editableUser.value = _user.value?.copy() // Reinicia la copia para editar
        } else {
            // Si salimos sin guardar, podemos resetear editableUser a los datos originales
            _editableUser.value = _user.value?.copy()
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
    fun saveProfileChanges() {
        _isLoading.value = true
        _errorMessage.value = null
        _isSuccess.value = null
        viewModelScope.launch {
            val userId = auth.currentUser?.uid
            val userToSave = _editableUser.value

            if (userId != null && userToSave != null) {
                try {
                    // Crea un mapa con solo los campos que quieres actualizar
                    val updates = hashMapOf(
                        "nombre" to userToSave.nombre,
                        "apellidos" to userToSave.apellidos,
                        "fechaNacimiento" to userToSave.fechaNacimiento,
                        "peso" to userToSave.peso,
                        "altura" to userToSave.altura,
                        "genero" to userToSave.genero,
                        "actividad" to userToSave.actividad,
                        "objetivo" to userToSave.objetivo,
                        "email" to userToSave.email // El email podría no cambiar, pero lo incluimos
                    )

                    db.collection("usuarios").document(userId).update(updates as Map<String, Any>).await()
                    _user.value = userToSave // Actualiza el usuario original con los cambios guardados
                    _isEditing.value = false // Sale del modo edición
                    _isSuccess.value = "Perfil actualizado con éxito." // Establece el mensaje de éxito aquí
                } catch (e: Exception) {
                    _errorMessage.value = "Error al guardar los cambios: ${e.message}"
                    println("Error al guardar los cambios: ${e.message}")
                } finally {
                    _isLoading.value = false
                }
            } else {
                _errorMessage.value = "No se puede guardar: usuario no autenticado o datos no disponibles."
                _isLoading.value = false
            }
        }
    }
}