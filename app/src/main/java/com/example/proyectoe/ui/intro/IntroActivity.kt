// src/main/java/com/example/proyectoe/ui/intro/IntroActivity.kt
package com.example.proyectoe.ui.intro

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectoe.ui.auth.LoginActivity // Asegúrate de que esta Activity exista
import com.example.proyectoe.ui.auth.RegisterActivity

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IntroScreen(
                onStartClick = {
                    // Al dar click en el botón, va a LoginActivity
                    startActivity(Intent(this@IntroActivity, LoginActivity::class.java))
                    finish() // Opcional: cierra IntroActivity si no quieres volver
                },
                onFooterSignInClick = {
                    // <--- ¡CAMBIO AQUÍ! Al dar click en el texto del footer, va a RegisterActivity
                    startActivity(Intent(this@IntroActivity, RegisterActivity::class.java))
                    finish() // Opcional: cierra IntroActivity si no quieres volver
                }
            )
        }
    }
}