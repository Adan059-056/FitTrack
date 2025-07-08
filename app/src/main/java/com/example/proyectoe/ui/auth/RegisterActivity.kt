// src/main/java/com/example/proyectoe/ui/auth/RegisterActivity.kt
package com.example.proyectoe.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectoe.ui.dashboard.MainActivity // Si el registro exitoso va a MainActivity

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RegisterScreen(
                onRegisterSuccess = {
                    // Si el registro es exitoso, ve a la MainActivity
                    startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                    finish()
                },
                onNavigateBack = {
                    // Puedes volver a LoginActivity si es tu flujo deseado
                    finish() // Simplemente cierra RegisterActivity para volver a la anterior (LoginActivity)
                    // o puedes: startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                }
            )
        }
    }
}