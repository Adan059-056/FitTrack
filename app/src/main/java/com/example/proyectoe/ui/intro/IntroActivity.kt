package com.example.proyectoe.ui.intro

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectoe.ui.auth.LoginActivity
import com.example.proyectoe.ui.auth.RegisterActivity


class IntroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Llamamos a Comprobarsesion() directamente aquí.
        // Si hay un usuario logueado, esta función debería redirigir *antes* de que se muestre el IntroScreen.

        // Si Comprobarsesion() no redirigió (porque no hay sesión), entonces se muestra el IntroScreen.
        setContent {
            IntroScreen(
                onStartClick = {
                    // Si llegamos aquí, es porque Comprobarsesion() determinó que NO hay un usuario logueado.
                    // O el usuario quiere "empezar" a loguearse/registrarse.
                    startActivity(Intent(this@IntroActivity, LoginActivity::class.java))
                    finishAffinity() // Cerramos IntroActivity completamente
                },
                onFooterSignInClick = {
                    startActivity(Intent(this@IntroActivity, RegisterActivity::class.java))
                    finishAffinity() // Cerramos IntroActivity completamente
                }
            )
        }
    }


}