package com.example.proyectoe.data.sensor

// data/sensor/StepCounterDataSource.kt


import kotlinx.coroutines.flow.Flow

interface StepCounterDataSource {
    // Un Flow es ideal para datos continuos como los pasos
    fun getSteps(): Flow<Int> // Emitirá el número actual de pasos
    fun startCounting() // Iniciar la escucha del sensor
    fun stopCounting()  // Detener la escucha del sensor
    fun resetSteps()    // Si tu contador permite resetear
}