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
    private val KEY_STEPS_AT_LAST_SAVE = "pasos_al_ultimo_guardado"
    private val KEY_DAILY_STEPS = "pasos_diarios"

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _currentDailySteps = MutableStateFlow(0)
    val currentDailySteps: StateFlow<Int> = _currentDailySteps

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // Valor del sensor la última vez que se leyó
    private var lastTotalStepsFromSensor = -1

    fun init() {
        loadDailySteps()
    }

    // sincroniza los pasos de firebase
    fun syncWithSavedSteps(savedSteps: Int) {
        val today = dateFormat.format(Date())
        sharedPrefs.edit().apply {
            // Sincronizamos el contador interno con el valor de Firestore
            putInt(KEY_DAILY_STEPS, savedSteps)
            // Ajustamos el offset para que el siguiente cálculo sea correcto
            putInt(KEY_STEPS_AT_LAST_SAVE, lastTotalStepsFromSensor - savedSteps)
            putString(KEY_LAST_SAVED_DATE, today)
            apply()
        }
        _currentDailySteps.value = savedSteps
        Log.d("StepRepo", "Sincronizado con Firestore. Pasos: $savedSteps, Nuevo offset: ${lastTotalStepsFromSensor - savedSteps}")
    }

    private fun loadDailySteps() {
        val today = dateFormat.format(Date())
        val lastSavedDate = sharedPrefs.getString(KEY_LAST_SAVED_DATE, null)

        if (today != lastSavedDate) {
            _currentDailySteps.value = 0
            sharedPrefs.edit().apply {
                putString(KEY_LAST_SAVED_DATE, today)
                putInt(KEY_STEPS_AT_LAST_SAVE, -1)
                putInt(KEY_DAILY_STEPS, 0)
                apply()
            }
            Log.d("StepRepo", "Nuevo dia detectado. Pasos diarios reiniciados a 0.")
        } else {
            _currentDailySteps.value = sharedPrefs.getInt(KEY_DAILY_STEPS, 0)
            Log.d("StepRepo", "Mismo dia. Cargando pasos diarios: ${_currentDailySteps.value}")
        }
    }

    fun updateTotalSteps(totalSensorSteps: Int) {
        lastTotalStepsFromSensor = totalSensorSteps // Guardamos el valor para la sincronización

        val today = dateFormat.format(Date())
        val lastSavedDate = sharedPrefs.getString(KEY_LAST_SAVED_DATE, null)
        var stepsAtDayStart = sharedPrefs.getInt(KEY_STEPS_AT_LAST_SAVE, -1)

        val editor = sharedPrefs.edit()

        if (today != lastSavedDate || stepsAtDayStart == -1) {
            editor.putInt(KEY_STEPS_AT_LAST_SAVE, totalSensorSteps)
            editor.putString(KEY_LAST_SAVED_DATE, today)
            _currentDailySteps.value = 0
            editor.putInt(KEY_DAILY_STEPS, _currentDailySteps.value)
            editor.apply()
            Log.d("StepRepo", "Nuevo dia o primer dato del sensor. Estableciendo offset del sensor en: $totalSensorSteps, Pasos diarios a 0.")
            return
        }

        if (totalSensorSteps < stepsAtDayStart) {
            val previousDailySteps = sharedPrefs.getInt(KEY_DAILY_STEPS, 0)
            val stepsAfterReboot = totalSensorSteps
            _currentDailySteps.value = previousDailySteps + stepsAfterReboot
            editor.putInt(KEY_STEPS_AT_LAST_SAVE, totalSensorSteps)
            editor.putInt(KEY_DAILY_STEPS, _currentDailySteps.value)
            editor.apply()
            Log.d("StepRepo", "Dispositivo reiniciado. Pasos previos: $previousDailySteps, Pasos desde reinicio del sensor: $stepsAfterReboot, Total hoy: ${_currentDailySteps.value}")
        }
        else {
            _currentDailySteps.value = totalSensorSteps - stepsAtDayStart
            editor.putInt(KEY_DAILY_STEPS, _currentDailySteps.value)
            editor.apply()
            Log.d("StepRepo", "Mismo dia, sin reinicio. Total Sensor: $totalSensorSteps, Offset del dia: $stepsAtDayStart, Pasos hoy: ${_currentDailySteps.value}")
        }
    }

    fun resetDailySteps() {
        sharedPrefs.edit().apply {
            putString(KEY_LAST_SAVED_DATE, "")
            putInt(KEY_DAILY_STEPS, 0)
            putInt(KEY_STEPS_AT_LAST_SAVE, -1)
            apply()
        }
        _currentDailySteps.value = 0
        Log.d("StepRepo", "Conteo diario reiniciado manualmente.")
    }
}
