package com.example.proyectoe.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.proyectoe.MyApplication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val stepCounterRepository = (application as MyApplication).stepCounterRepository

    private var saveStepsJob: Job? = null

    //liveData para que su valor pueda cambiar
    private val _dailyStepGoal = MutableLiveData(10000)
    val dailyStepGoal: LiveData<Int> = _dailyStepGoal

    private val _currentSteps = MutableLiveData(0)
    val currentSteps: LiveData<Int> = _currentSteps

    val distanceKm: LiveData<Float> = currentSteps.map { steps ->
        val strideLengthMeters = 0.76f
        val distanceMeters = steps * strideLengthMeters
        val distanceKm = distanceMeters / 1000f
        String.format("%.2f", distanceKm).toFloat()
    }

    val stepProgressPercentage: LiveData<Float> = currentSteps.map { steps ->
        val goal = dailyStepGoal.value ?: 10000
        if (goal > 0) (steps.toFloat() / goal.toFloat()) * 100f else 0f
    }

    val stepsRemaining: LiveData<Float> = currentSteps.map { steps ->
        // Usa el valor dinámico del objetivo de pasos
        val goal = dailyStepGoal.value ?: 10000
        val remaining = goal - steps
        if (remaining < 0) 0f else remaining.toFloat()
    }

    fun loadStepsAndStartSavingForUser(userId: String) {
        println("DashboardViewModel: Cargando pasos para el usuario: $userId")
        _currentSteps.value = 0
        saveStepsJob?.cancel()

        viewModelScope.launch {
            try {
                // carga los datos del usuario, incluyendo el objetivo
                val userDoc = db.collection("usuarios").document(userId).get().await()
                if (userDoc.exists()) {
                    val objetivo = userDoc.getString("objetivo") ?: "Mantener peso"
                    calculateStepsGoal(objetivo)
                }
            } catch (e: Exception) {
                println("Error al cargar datos del usuario para el dashboard: $e")
            }

            // escucha los cambios en los pasos
            db.collection("usuarios").document(userId)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        println("Error al escuchar cambios en pasos del usuario: $e")
                        return@addSnapshotListener
                    }
                    val fetchedSteps = snapshot?.getLong("pasosDiarios")?.toInt() ?: 0
                    stepCounterRepository.syncWithSavedSteps(fetchedSteps)
                    if (_currentSteps.value != fetchedSteps) {
                        _currentSteps.postValue(fetchedSteps)
                    }
                }
        }

        saveStepsJob = viewModelScope.launch {
            stepCounterRepository.currentDailySteps
                .debounce(5000L)
                .collectLatest { steps ->
                    val currentUser = auth.currentUser
                    if (currentUser?.uid == userId) {
                        db.collection("usuarios").document(userId)
                            .update("pasosDiarios", steps)
                            .addOnSuccessListener {
                                println("Pasos guardados exitosamente en Firestore: $steps")
                            }
                            .addOnFailureListener { e ->
                                println("Error al guardar pasos en Firestore: $e")
                            }
                    }
                }
        }
    }

    // para calcular el objetivo de pasos segun los objetivos
    private fun calculateStepsGoal(objetivo: String) {
        val newGoal = when (objetivo) {
            "Perder peso" -> 12000
            "Ganar músculo" -> 8000
            else -> 10000
        }
        _dailyStepGoal.postValue(newGoal)
    }

    override fun onCleared() {
        super.onCleared()
        saveStepsJob?.cancel()
        println("DashboardViewModel: onCleared - ViewModel destruido.")
    }
}