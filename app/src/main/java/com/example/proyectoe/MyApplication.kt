package com.example.proyectoe

import android.app.Application
import android.content.Intent
import android.os.Build
import com.example.proyectoe.data.repository.StepCounterRepository
import com.example.proyectoe.StepCounterService

class MyApplication : Application() {

    val stepCounterRepository: StepCounterRepository by lazy {
        StepCounterRepository(this)
    }

    override fun onCreate() {
        super.onCreate()
        // Inicializamos el repository
        stepCounterRepository.init()

        // Iniciamos el segundo plano
        val serviceIntent = Intent(this, StepCounterService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }
}