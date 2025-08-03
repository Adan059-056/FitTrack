package com.example.proyectoe

import android.app.Application
import android.content.Intent
import android.os.Build
import com.example.proyectoe.data.repository.StepCounterRepository
import com.example.proyectoe.StepCounterService
import com.example.proyectoe.data.datasource.notificaciones.NotificationScheduler

class MyApplication : Application() {

    val stepCounterRepository: StepCounterRepository by lazy {
        StepCounterRepository(this)
    }

    override fun onCreate() {
        super.onCreate()
        // Inicializa el repository
        stepCounterRepository.init()

        // Iniciamos el segundo plano
        val serviceIntent = Intent(this, StepCounterService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }

        // Desayuno a las 9:00
        NotificationScheduler.scheduleDailyNotification(
            this,
            9,
            0,
            "¡Es hora del desayuno! Registra tus alimentos.",
            1
        )
        // Almuerzo a las 13:00
        NotificationScheduler.scheduleDailyNotification(
            this,
            13,
            0,
            "¡Hora del almuerzo! Registra tus alimentos.",
            2
        )
        // Cena a las 8:00 PM
        NotificationScheduler.scheduleDailyNotification(
            this,
            20,
            0,
            "¡Hora de la cena! Registra tus alimentos.",
            3
        )


    }

}