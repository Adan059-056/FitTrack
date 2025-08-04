package com.example.proyectoe.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StepCounterRepository(private val context: Context) {

    private val PREFS_NAME = "contador_pasos_prefs"
    private val KEY_LAST_SAVED_DATE = "ultima_fecha_guardada"
    private val KEY_STEPS_OFFSET = "pasos_offset"
    private val KEY_DAILY_STEPS = "pasos_diarios"

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _currentDailySteps = MutableStateFlow(0)
    val currentDailySteps: StateFlow<Int> = _currentDailySteps

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private var stepsOffset = 0

    fun init() {
        loadStepsState()
    }

    private fun loadStepsState() {
        stepsOffset = sharedPrefs.getInt(KEY_STEPS_OFFSET, -1)
        _currentDailySteps.value = sharedPrefs.getInt(KEY_DAILY_STEPS, 0)
        Log.d("StepRepo", "Estado cargado. Pasos diarios: ${_currentDailySteps.value}, Offset: $stepsOffset")
    }

    fun updateTotalSteps(totalSensorSteps: Int) {
        val today = dateFormat.format(Date())
        val lastSavedDate = sharedPrefs.getString(KEY_LAST_SAVED_DATE, null)

        if (lastSavedDate == null) {
            Log.d("StepRepo", "Sesión nueva. Inicializando el estado con el primer valor del sensor.")

            stepsOffset = totalSensorSteps - _currentDailySteps.value

            with(sharedPrefs.edit()) {
                putString(KEY_LAST_SAVED_DATE, today)
                putInt(KEY_STEPS_OFFSET, stepsOffset)
                apply()
            }
        }
        else if (today != lastSavedDate) {
            Log.d("StepRepo", "¡Nuevo día detectado! Reiniciando contador de pasos.")
            stepsOffset = totalSensorSteps
            _currentDailySteps.value = 0
            with(sharedPrefs.edit()) {
                putString(KEY_LAST_SAVED_DATE, today)
                putInt(KEY_STEPS_OFFSET, stepsOffset)
                putInt(KEY_DAILY_STEPS, 0)
                apply()
            }
        }

        // El resto de la lógica para actualizar el contador funciona igual.
        val dailySteps = totalSensorSteps - stepsOffset

        if (dailySteps > _currentDailySteps.value) {
            _currentDailySteps.value = dailySteps
            with(sharedPrefs.edit()) {
                putInt(KEY_DAILY_STEPS, dailySteps)
                apply()
            }
            Log.d("StepRepo", "Pasos diarios actualizados: $dailySteps")
        } else {
            Log.d("StepRepo", "El nuevo cálculo ($dailySteps) no es mayor que el actual (${_currentDailySteps.value}). No se actualiza.")
        }

    }

    fun syncWithSavedSteps(savedSteps: Int) {
        if (savedSteps > _currentDailySteps.value) {
            _currentDailySteps.value = savedSteps

            with(sharedPrefs.edit()) {
                putInt(KEY_DAILY_STEPS, savedSteps)
                apply()
            }
            Log.d("StepRepo", "Sincronizado con pasos guardados. Pasos actualizados a: $savedSteps")
        } else {
            Log.d("StepRepo", "El valor de guardado ($savedSteps) no es mayor que el actual (${_currentDailySteps.value}). No se actualiza.")
        }
    }

    fun resetStateForUserChange() {
        Log.d("StepRepo", "Reiniciando el estado del contador debido a un cambio de usuario.")

        with(sharedPrefs.edit()) {
            clear()
            apply()
        }

        _currentDailySteps.value = 0
        stepsOffset = -1
    }
}
