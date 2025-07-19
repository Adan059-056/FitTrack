// src/main/java/com/example/proyectoe/ui/auth/LoginActivity.kt
package com.example.proyectoe.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
// ¡Asegúrate de importar correctamente tu LoginScreen (el que quieres usar)!
import com.example.proyectoe.ui.auth.LoginScreen // Este import apunta al archivo LoginScreen.kt
import com.example.proyectoe.ui.dashboard.MainActivity
import com.example.proyectoe.ui.intro.IntroActivity


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // ¡Aquí es donde se llama a tu LoginScreen completo!
            LoginScreen(
                onLoginSuccess = {
                    // Cuando el login es exitoso en LoginScreen, navegamos a MainActivity.
                    // Usamos finishAffinity() para limpiar completamente el stack y que MainActivity sea la nueva raíz.
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finishAffinity()
                },
                onNavigateIntro = {
                    // Si el usuario elige "volver" desde la pantalla de login, lo mandamos a IntroActivity.
                    // finish() es suficiente aquí si solo quieres salir de LoginActivity.
                    startActivity(Intent(this@LoginActivity, IntroActivity::class.java))
                    finish()
                },
                onNavigateBack = {
                    // Este callback es para "No tienes cuenta? Regístrate". Lo enviamos a RegisterActivity.
                    startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
                    finishAffinity() // Cierra LoginActivity y va a RegisterActivity como nueva raíz.
                }
            )
        }
    }
}
