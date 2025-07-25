// src/main/java/com/example/proyectoe/ui/auth/RegisterActivity.kt
package com.example.proyectoe.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectoe.ui.dashboard.MainActivity // Si el registro exitoso va a MainActivity
import com.example.proyectoe.ui.intro.IntroActivity

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
                onNavigateIntro = {
                    startActivity(Intent(this@RegisterActivity,IntroActivity::class.java))
                },
                onNavigateBack = {
                    // Puedes volver a LoginActivity si es tu flujo deseado
                    //finish() // Simplemente cierra RegisterActivity para volver a la anterior (LoginActivity)
                  startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                }
            )
        }
    }
}