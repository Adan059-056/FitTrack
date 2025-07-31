package com.example.proyectoe.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    fun init() {
        loadDailySteps()
    }

    // Carga los pasos diarios guardados desde SharedPreferences o los reinicia si es un nuevo día.
    private fun loadDailySteps() {
        val today = dateFormat.format(Date())
        val lastSavedDate = sharedPrefs.getString(KEY_LAST_SAVED_DATE, null)

        if (today != lastSavedDate) {
            _currentDailySteps.value = 0
            sharedPrefs.edit().putString(KEY_LAST_SAVED_DATE, today).apply()
            sharedPrefs.edit().putInt(KEY_STEPS_AT_LAST_SAVE, -1).apply()
            Log.d("StepRepo", "Nuevo dia detectado. Pasos diarios reiniciados a 0.")
        } else {
            _currentDailySteps.value = sharedPrefs.getInt(KEY_DAILY_STEPS, 0)
            Log.d("StepRepo", "Mismo dia. Cargando pasos diarios: ${_currentDailySteps.value}")
        }
    }

    // Actualiza el conteo de pasos diarios en base al valor total recibido del sensor.
    fun updateTotalSteps(totalSensorSteps: Int) {
        val today = dateFormat.format(Date())
        val lastSavedDate = sharedPrefs.getString(KEY_LAST_SAVED_DATE, null)
        var stepsAtDayStart = sharedPrefs.getInt(KEY_STEPS_AT_LAST_SAVE, -1)

        val editor = sharedPrefs.edit()

        // Nuevo día o primera vez que el sensor envía datos para hoy
        if (today != lastSavedDate || stepsAtDayStart == -1) {
            editor.putInt(KEY_STEPS_AT_LAST_SAVE, totalSensorSteps)
            editor.putString(KEY_LAST_SAVED_DATE, today)
            _currentDailySteps.value = 0 // El conteo diario para el nuevo día empieza en 0.
            editor.putInt(KEY_DAILY_STEPS, _currentDailySteps.value)
            editor.apply()
            Log.d("StepRepo", "Nuevo dia o primer dato del sensor. Estableciendo offset del sensor en: $totalSensorSteps, Pasos diarios a 0.")
            return
        }

        // si se reinicia el telefono
        if (totalSensorSteps < stepsAtDayStart) {
            val previousDailySteps = sharedPrefs.getInt(KEY_DAILY_STEPS, 0)
            val stepsAfterReboot = totalSensorSteps
            _currentDailySteps.value = previousDailySteps + stepsAfterReboot
            editor.putInt(KEY_STEPS_AT_LAST_SAVE, stepsAfterReboot)
            editor.putInt(KEY_DAILY_STEPS, _currentDailySteps.value)
            editor.apply()
            Log.d("StepRepo", "Dispositivo reiniciado. Pasos previos: $previousDailySteps, Pasos desde reinicio del sensor: $stepsAfterReboot, Total hoy: ${_currentDailySteps.value}")
        }
        // mismo dia sin no hay reinicio
        else {
            //conteo de pasos acumulados para el día de hoy*
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
