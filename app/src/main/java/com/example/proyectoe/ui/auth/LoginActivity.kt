package com.example.proyectoe.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
//import com.example.proyectoe.ui.auth.LoginScreen
import com.example.proyectoe.ui.dashboard.MainActivity


class LoginActivity : AppCompatActivity (){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            LoginScreen (
                onLoginSuccess = {
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                },
                onNavigateToRegister = {

                }
            )




        }


        }

}