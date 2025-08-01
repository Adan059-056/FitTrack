package com.example.proyectoe.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectoe.ui.dashboard.MainActivity
import com.example.proyectoe.ui.intro.IntroActivity
import com.google.firebase.auth.FirebaseAuth
/*
    es una AppCompatActivity que se encarga de la lógica de inicio de la aplicación:
    verificar el estado de autenticación del usuario y redirigir a la MainActivity o IntroActivity
    según corresponda.
 */

class SplashActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // No es necesario un layout para esta actividad
        auth = FirebaseAuth.getInstance()

        // Verifica si hay un usuario autenticado
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Si el usuario está autenticado, ir a MainActivity
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            // Si no está autenticado, ir a introActivity
            startActivity(Intent(this, IntroActivity::class.java))
        }
        finish() // Cierra la actividad para no volver a ella
    }
}