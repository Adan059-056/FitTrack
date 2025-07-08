package com.example.proyectoe.ui.intro

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectoe.ui.auth.LoginActivity
import com.example.proyectoe.ui.dashboard.MainActivity
import com.example.proyectoe.ui.auth.LoginScreen

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            IntroScreen(onStartClick = {
                //al parecer aqui es donde si se le da click al botton mada a la siguiente actividad
                startActivity(Intent(this@IntroActivity, LoginActivity::class.java))
                finish()
            })
        }
    }
}

