package com.example.proyectoe.Activity.IntroActivity

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectoe.Activity.MainActivity.MainActivity

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            IntroScreen(onStartClick = {
                startActivity(Intent(this@IntroActivity, MainActivity::class.java))
            })
        }
    }
}