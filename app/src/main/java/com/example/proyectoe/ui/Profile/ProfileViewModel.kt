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

    // MutableStateFlow para mantener el estado del usuario, inicializado como null
    private val _user = MutableStateFlow<User?>(null)
    // StateFlow público para que la UI observe los cambios
    val user: StateFlow<User?> = _user

    // MutableStateFlow para manejar el estado de carga
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    // MutableStateFlow para manejar mensajes de error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    init {
        // Al inicializar la ViewModel, intentamos cargar los datos del usuario
        loadUserProfile()
    }

    fun loadUserProfile() {
        _isLoading.value = true // Indica que estamos cargando
        _errorMessage.value = null // Limpia cualquier mensaje de error anterior
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    val documentSnapshot = db.collection("usuarios").document(userId).get().await() // Usa await para suspender y esperar el resultado
                    if (documentSnapshot.exists()) {
                        val userData = documentSnapshot.toObject(User::class.java)
                        _user.value = userData // Actualiza el StateFlow con los datos del usuario
                    } else {
                        _errorMessage.value = "No se encontraron datos para el usuario."
                    }
                } else {
                    _errorMessage.value = "Usuario no autenticado."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar el perfil: ${e.message}"
                println("Error al cargar el perfil: ${e.message}") // Para depuración
            } finally {
                _isLoading.value = false // Finaliza la carga, ya sea exitosa o con error
            }
        }
    }
}