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
                
                //pasa el rollback para que cuando se le da al boton de no tienes cuenta mande al
                // register
                onNavigateBack = {
                    startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
                    finish()
                }
            )




        }


        }

}