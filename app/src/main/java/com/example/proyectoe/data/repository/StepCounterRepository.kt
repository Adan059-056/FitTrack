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
    private val KEY_STEPS_OFFSET = "pasos_offset" // Cambiado el nombre para mayor claridad
    private val KEY_DAILY_STEPS = "pasos_diarios"

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _currentDailySteps = MutableStateFlow(0)
    val currentDailySteps: StateFlow<Int> = _currentDailySteps

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // Valor del sensor al inicio del día. Este es el offset.
    private var stepsOffset = 0

    fun init() {
        loadDailySteps()
    }

    private fun loadDailySteps() {
        val today = dateFormat.format(Date())
        val lastSavedDate = sharedPrefs.getString(KEY_LAST_SAVED_DATE, null)

        if (today != lastSavedDate) {
            // Es un nuevo día. Reiniciamos el offset y el contador.
            stepsOffset = -1 // Usamos -1 como indicador de que necesitamos un nuevo offset
            _currentDailySteps.value = 0
            Log.d("StepRepo", "Nuevo dia. Reiniciando contador.")
        } else {
            // Es el mismo día. Cargamos el offset y los pasos guardados.
            stepsOffset = sharedPrefs.getInt(KEY_STEPS_OFFSET, -1)
            _currentDailySteps.value = sharedPrefs.getInt(KEY_DAILY_STEPS, 0)
            Log.d("StepRepo", "Mismo dia. Cargando pasos diarios: ${_currentDailySteps.value}, Offset: $stepsOffset")
        }
    }

    fun updateTotalSteps(totalSensorSteps: Int) {
        val today = dateFormat.format(Date())

        if (stepsOffset == -1) {
            // Esta es la primera lectura del sensor en el día.
            // Establecemos el offset y guardamos la fecha.
            stepsOffset = totalSensorSteps
            _currentDailySteps.value = 0
            with(sharedPrefs.edit()) {
                putString(KEY_LAST_SAVED_DATE, today)
                putInt(KEY_STEPS_OFFSET, stepsOffset)
                putInt(KEY_DAILY_STEPS, _currentDailySteps.value)
                apply()
            }
            Log.d("StepRepo", "Primer dato del sensor del dia. Offset establecido a: $stepsOffset")
            return
        }

        // Si el valor del sensor es menor que el offset, significa que el dispositivo se ha reiniciado.
        // En este caso, el offset ya no es válido para el día.
        if (totalSensorSteps < stepsOffset) {
            Log.d("StepRepo", "Dispositivo reiniciado. El sensor ha vuelto a 0. Ajustando offset.")
            // Para mantener la consistencia, el nuevo offset será el totalSensorSteps.
            // Los pasos previos del día ya estaban guardados, así que la lógica es correcta.
            stepsOffset = totalSensorSteps
            with(sharedPrefs.edit()) {
                putInt(KEY_STEPS_OFFSET, stepsOffset)
                apply()
            }
        }

        // Calculamos los pasos diarios y actualizamos el valor
        val dailySteps = totalSensorSteps - stepsOffset
        _currentDailySteps.value = dailySteps

        // Guardamos los pasos diarios de forma persistente
        with(sharedPrefs.edit()) {
            putInt(KEY_DAILY_STEPS, dailySteps)
            apply()
        }
        Log.d("StepRepo", "Pasos diarios actualizados: $dailySteps")
    }

    fun syncWithSavedSteps(savedSteps: Int) {
        // Solo actualizamos los pasos si el valor de Firestore es mayor
        if (savedSteps > _currentDailySteps.value) {
            _currentDailySteps.value = savedSteps

            // También guardamos el valor en SharedPreferences
            with(sharedPrefs.edit()) {
                putInt(KEY_DAILY_STEPS, savedSteps)
                apply()
            }
            Log.d("StepRepo", "Sincronizado con Firestore. Pasos actualizados a: $savedSteps")
        } else {
            Log.d("StepRepo", "El valor de Firestore ($savedSteps) no es mayor que el actual (${_currentDailySteps.value}). No se actualiza.")
        }
    }

    fun resetDailySteps() {
        with(sharedPrefs.edit()) {
            putString(KEY_LAST_SAVED_DATE, "")
            putInt(KEY_DAILY_STEPS, 0)
            putInt(KEY_STEPS_OFFSET, -1)
            apply()
        }
        _currentDailySteps.value = 0
        Log.d("StepRepo", "Conteo diario reiniciado manualmente.")
    }


}
