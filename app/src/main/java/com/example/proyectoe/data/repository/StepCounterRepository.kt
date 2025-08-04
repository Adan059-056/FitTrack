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
        loadDailySteps()
    }

    private fun loadDailySteps() {
        val today = dateFormat.format(Date())
        val lastSavedDate = sharedPrefs.getString(KEY_LAST_SAVED_DATE, null)

        if (today != lastSavedDate) {
            stepsOffset = -1
            _currentDailySteps.value = 0
            Log.d("StepRepo", "Nuevo dia. Reiniciando contador.")
        } else {
            stepsOffset = sharedPrefs.getInt(KEY_STEPS_OFFSET, -1)
            _currentDailySteps.value = sharedPrefs.getInt(KEY_DAILY_STEPS, 0)
            Log.d("StepRepo", "Mismo dia. Cargando pasos diarios: ${_currentDailySteps.value}, Offset: $stepsOffset")
        }
    }

    fun updateTotalSteps(totalSensorSteps: Int) {
        val today = dateFormat.format(Date())

        if (stepsOffset == -1) {
            Log.d("StepRepo", "El offset no ha sido establecido.")
            // Si el dailySteps ya es > 0 se calcula el offset para que el sensor no sobrescriba los pasos
            if (_currentDailySteps.value > 0) {
                stepsOffset = totalSensorSteps - _currentDailySteps.value
                Log.d("StepRepo", "Pasos cargados de Firestore. Calculando offset: $stepsOffset")
            } else {
                // Si dailySteps es 0, es el inicio del día o del conteo.
                stepsOffset = totalSensorSteps
                _currentDailySteps.value = 0
                Log.d("StepRepo", "Primer dato del sensor del dia. Offset establecido a: $stepsOffset")
            }

            // se guarda el nuevo offset y la fecha
            with(sharedPrefs.edit()) {
                putString(KEY_LAST_SAVED_DATE, today)
                putInt(KEY_STEPS_OFFSET, stepsOffset)
                apply()
            }
        }


        // Si el valor del sensor es menor que el offset, significa que el dispositivo se ha reiniciado.
        if (totalSensorSteps < stepsOffset) {
            Log.d("StepRepo", "Dispositivo reiniciado. El sensor ha vuelto a 0. Ajustando offset.")
            stepsOffset = totalSensorSteps
            with(sharedPrefs.edit()) {
                putInt(KEY_STEPS_OFFSET, stepsOffset)
                apply()
            }
        }

        // Calculamos los pasos diarios y actualizamos el valor
        val dailySteps = totalSensorSteps - stepsOffset

        // se actualiza si el nuevo valor es mayor que el actual evitando que una lectura del sensor más lenta sobrescriba el valor de firbase
        if (dailySteps > _currentDailySteps.value) {
            _currentDailySteps.value = dailySteps

            // para guardar los paoss
            with(sharedPrefs.edit()) {
                putInt(KEY_DAILY_STEPS, dailySteps)
                apply()
            }
            Log.d("StepRepo", "Pasos diarios actualizados: $dailySteps")
        } else {
            Log.d("StepRepo", "Nuevo cálculo ($dailySteps) no es mayor que el actual (${_currentDailySteps.value}). No se actualiza.")
        }
    }

    fun syncWithSavedSteps(savedSteps: Int) {
        if (savedSteps > _currentDailySteps.value) {
            _currentDailySteps.value = savedSteps

            with(sharedPrefs.edit()) {
                putInt(KEY_DAILY_STEPS, savedSteps)
                apply()
            }
            Log.d("StepRepo", "Sincronizado con Firestore. Pasos actualizados a: $savedSteps")
        } else {
            Log.d("StepRepo", "El valor de Firestore ($savedSteps) no es mayor que el actual (${_currentDailySteps.value}). No se actualiza.")
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
