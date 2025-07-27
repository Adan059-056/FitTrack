package com.example.proyectoe.ui.auth

//Aqui va la logica de autentificacion
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthResult {
    object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
    object Loading : AuthResult()
    object Idle : AuthResult()
}

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow<AuthResult>(AuthResult.Idle)
    val authState: StateFlow<AuthResult> get() = _authState

    fun login(email: String, password: String) {
        _authState.value = AuthResult.Loading
        viewModelScope.launch {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    _authState.value = if (task.isSuccessful) {
                        AuthResult.Success
                    } else {
                        AuthResult.Error(task.exception?.message ?: "Login failed")
                    }
                }
        }
    }

    fun register(email: String, password: String) {
        _authState.value = AuthResult.Loading
        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    _authState.value = if (task.isSuccessful) {
                        AuthResult.Success
                    } else {
                        AuthResult.Error(task.exception?.message ?: "Registration failed")
                    }
                }
        }
    }

    fun logout() {
        auth.signOut()
        _authState.value = AuthResult.Idle
    }
}
