package com.example.proyectoe.data.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.content.SharedPreferences

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ManejoContadorPasos(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepCounterSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    private var onStepCountChanged: ((stepCount: Float) -> Unit)? = null

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("step_counter_prefs", Context.MODE_PRIVATE)

    private val KEY_DAILY_STEPS_OFFSET = "daily_steps_offset"
    private val KEY_LAST_COUNT_DATE = "last_count_date"
    private val KEY_STEPS_ACCUMULATED_TODAY = "steps_accumulated_today"


    // Variables en memoria
    private var dailyStepsOffset = 0f
    private var lastRecordedDate = ""
    private var stepsAccumulatedTodayInMemory = 0f
    init {

        loadState()
    }

    fun startListening(onChange: (stepCount: Float) -> Unit) {
        onStepCountChanged = onChange
        stepCounterSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        onStepCountChanged?.invoke(stepsAccumulatedTodayInMemory)
        //println("ManejoContadorPasos: Inicia escucha. Pasos actuales cargados: $stepsAccumulatedTodayInMemory")
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
        saveState()
        //println("ManejoContadorPasos: Detiene escucha. Estado guardado.")
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                val currentSensorTotalSteps = it.values[0]

                val calendar = Calendar.getInstance()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val today = dateFormat.format(calendar.time)

                //Maneja el cambio de día
                if (lastRecordedDate != today) {
                    dailyStepsOffset = currentSensorTotalSteps // El offset es el total del sensor en este momento
                    stepsAccumulatedTodayInMemory = 0f // Reinicia el conteo
                    lastRecordedDate = today
                    saveState() // Guardar el nuevo offset y la nueva fecha
                    println("ManejoContadorPasos: Nuevo día detectado o primera lectura del día. Conteo reiniciado. Offset: $dailyStepsOffset")
                }

                var currentDailySteps = currentSensorTotalSteps - dailyStepsOffset

                if (currentDailySteps < 0) {
                    println("ManejoContadorPasos: Sensor probablemente reiniciado. Ajustando offset.")
                    dailyStepsOffset = currentSensorTotalSteps // El nuevo offset es el valor actual del sensor
                    currentDailySteps = 0f
                    saveState()
                }

                stepsAccumulatedTodayInMemory = currentDailySteps
                onStepCountChanged?.invoke(stepsAccumulatedTodayInMemory)
                saveState() //guadra los datos para que persistan los pasos
                println("ManejoContadorPasos: Sensor total: $currentSensorTotalSteps, Offset: $dailyStepsOffset, Pasos Hoy: $stepsAccumulatedTodayInMemory")
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    fun hasStepCounterSensor(): Boolean {
        return stepCounterSensor != null
    }

    fun getStepsAccumulatedToday(): Float {
        return stepsAccumulatedTodayInMemory
    }

    private fun saveState() {
        sharedPreferences.edit().apply {
            putFloat(KEY_DAILY_STEPS_OFFSET, dailyStepsOffset)
            putString(KEY_LAST_COUNT_DATE, lastRecordedDate)
            putFloat(KEY_STEPS_ACCUMULATED_TODAY, stepsAccumulatedTodayInMemory)
            apply()
        }
        println("ManejoContadorPasos: Estado guardado. Offset: $dailyStepsOffset, Fecha: $lastRecordedDate, Pasos Hoy: $stepsAccumulatedTodayInMemory")
    }

    private fun loadState() {
        dailyStepsOffset = sharedPreferences.getFloat(KEY_DAILY_STEPS_OFFSET, 0f)
        lastRecordedDate = sharedPreferences.getString(KEY_LAST_COUNT_DATE, "") ?: ""
        stepsAccumulatedTodayInMemory = sharedPreferences.getFloat(KEY_STEPS_ACCUMULATED_TODAY, 0f)
        println("ManejoContadorPasos: Estado cargado. Offset: $dailyStepsOffset, Fecha: $lastRecordedDate, Pasos Hoy: $stepsAccumulatedTodayInMemory")
    }

    fun resetDailySteps() {
        sharedPreferences.edit().apply {
            putString(KEY_LAST_COUNT_DATE, "")
            putFloat(KEY_STEPS_ACCUMULATED_TODAY, 0f) // Resetea los pasos acumulados
            apply()
        }
        // Actualizar variables en memoria para reflejar el reinicio inmediatamente
        dailyStepsOffset = 0f
        lastRecordedDate = ""
        stepsAccumulatedTodayInMemory = 0f
        onStepCountChanged?.invoke(0f)
        println("ManejoContadorPasos: Conteo diario reiniciado manualmente.")
    }
}
