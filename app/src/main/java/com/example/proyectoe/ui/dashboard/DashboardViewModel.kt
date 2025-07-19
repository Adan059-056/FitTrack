package com.example.proyectoe.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.proyectoe.data.sensor.ManejoContadorPasos
import androidx.lifecycle.map

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    //PARA EL CONTADOR DE PASOS
    private val _currentSteps = MutableLiveData(0f)
    val currentSteps: LiveData<Float> = _currentSteps

    private val manejoContadorPasos = ManejoContadorPasos(application)

    val distanceKm: LiveData<Float> = currentSteps.map { steps -> // <-- Aquí el cambio de sintaxis
        val strideLengthMeters = 0.76f
        val distanceMeters = steps * strideLengthMeters
        val distanceKm = distanceMeters / 1000f
        String.format("%.2f", distanceKm).toFloat()
    }
    //PARA LA GRAFICA
    val dailyStepGoal = 10000f

    val stepProgressPercentage: LiveData<Float> = currentSteps.map { steps ->
        if (dailyStepGoal > 0) (steps / dailyStepGoal) * 100f else 0f
    }

    //LiveData para los pasos restantes
    val stepsRemaining: LiveData<Float> = currentSteps.map { steps ->
        val remaining = dailyStepGoal - steps
        if (remaining < 0) 0f else remaining
    }

    init {
        _currentSteps.value = manejoContadorPasos.getStepsAccumulatedToday()
    }

    fun startStepCounting() {
        if (manejoContadorPasos.hasStepCounterSensor()) {
            manejoContadorPasos.startListening { steps ->
                // Actualiza el LiveData con el nuevo conteo de pasos.
                _currentSteps.postValue(steps)
            }
            println("DashboardViewModel: Contador de pasos iniciado.")
        } else {
            println("DashboardViewModel: Dispositivo no tiene sensor de contador de pasos.")
            _currentSteps.postValue(-1f)
        }
    }

    fun stopStepCounting() {
        manejoContadorPasos.stopListening()
        println("DashboardViewModel: Contador de pasos detenido.")
    }

    fun resetDailySteps() {
        manejoContadorPasos.resetDailySteps()
    }

    override fun onCleared() {
        super.onCleared()
        // detiene la escucha del sensor para evitar fugas de memoria
        stopStepCounting()
        println("DashboardViewModel: onCleared - Sensor detenido.")
    }
}