package com.example.proyectoe.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.proyectoe.data.datasource.local.sensor.ManejoContadorPasos
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.proyectoe.MyApplication
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    //PARA EL CONTADOR DE PASOS

    private val stepCounterRepository = (application as MyApplication).stepCounterRepository

    private val _currentSteps = MutableLiveData(0)
    val currentSteps: LiveData<Int> = _currentSteps

    private val manejoContadorPasos = ManejoContadorPasos(application)

    val distanceKm: LiveData<Float> = currentSteps.map { steps ->
        val strideLengthMeters = 0.76f
        val distanceMeters = steps * strideLengthMeters
        val distanceKm = distanceMeters / 1000f
        String.format("%.2f", distanceKm).toFloat()
    }
    // objetivo diari de pasos
    val dailyStepGoal = 10000f

    val stepProgressPercentage: LiveData<Float> = currentSteps.map { steps ->
        if (dailyStepGoal > 0) (steps / dailyStepGoal) * 100f else 0f
    }

    val stepsRemaining: LiveData<Float> = currentSteps.map { steps ->
        val remaining = dailyStepGoal - steps
        if (remaining < 0) 0f else remaining
    }

    init {
        _currentSteps.value = stepCounterRepository.currentDailySteps.value
        viewModelScope.launch {
            stepCounterRepository.currentDailySteps.collectLatest { steps ->
                // Actualiza el LiveData cuando el StateFlow del repositorio emite un nuevo valor.
                _currentSteps.postValue(steps)
                println("DashboardViewModel: Pasos actualizados desde repositorio: $steps")
            }
        }
    }

    fun resetDailySteps() {
        stepCounterRepository.resetDailySteps()
        println("DashboardViewModel: Solicitado reinicio de pasos diarios.")
    }

    override fun onCleared() {
        super.onCleared()
        println("DashboardViewModel: onCleared - ViewModel destruido.")
    }
}