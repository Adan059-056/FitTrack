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
    private val stepCounterSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    // Callback para cuando el valor del contador de pasos cambia
    private var onStepCountChanged: ((stepCount: Float) -> Unit)? = null

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("step_counter_prefs", Context.MODE_PRIVATE)

    private var initialSensorReadingAtStartOfDay = 0f
    private var stepsAccumulatedToday = 0f
    private var initialStepCount = 0f
    private var isInitialCountSet = false
    private var lastSensorTotalSteps = 0f

    private val KEY_INITIAL_SENSOR_READING_AT_START_OF_DAY = "initial_sensor_reading_at_start_of_day"
    private val KEY_STEPS_ACCUMULATED_TODAY = "steps_accumulated_today"
    private val KEY_LAST_COUNT_DATE = "last_count_date"

    init {
        loadStepCountState()
    }

    fun startListening(onChange: (stepCount: Float) -> Unit) {
        onStepCountChanged = onChange
        stepCounterSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        onStepCountChanged?.invoke(stepsAccumulatedToday)
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
        saveStepCountState()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                val currentSensorTotalSteps = it.values[0]

                val calendar = Calendar.getInstance()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val today = dateFormat.format(calendar.time)

                val lastCountDate = sharedPreferences.getString(KEY_LAST_COUNT_DATE, "")

                if (lastCountDate != today) {
                    // cuando cambia el dia
                    initialSensorReadingAtStartOfDay = currentSensorTotalSteps
                    stepsAccumulatedToday = 0f
                    sharedPreferences.edit().putString(KEY_LAST_COUNT_DATE, today).apply()
                    println("ManejoContadorPasos: Nuevo día detectado. Conteo reiniciado a 0.")
                } else if (initialSensorReadingAtStartOfDay == 0f) {
                    // Si el initialSensorReadingAtStartOfDay es 0, significa que la app acaba de arrancar por primera vez en el día
                    // o que el dispositivo se reinició
                    initialSensorReadingAtStartOfDay = currentSensorTotalSteps - stepsAccumulatedToday
                    println("ManejoContadorPasos: Initial reading set on resume: $initialSensorReadingAtStartOfDay")
                }

                // Calcular los pasos para mostrar en la UI
                val currentDaySteps = currentSensorTotalSteps - initialSensorReadingAtStartOfDay

                stepsAccumulatedToday = if (currentDaySteps < 0) 0f else currentDaySteps
                onStepCountChanged?.invoke(stepsAccumulatedToday)
                println("ManejoContadorPasos: Current sensor: $currentSensorTotalSteps, Initial reading: $initialSensorReadingAtStartOfDay, Steps Today: $stepsAccumulatedToday")
            }
        }
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    // verifica si el dispositivo tiene el sensor de pasos
    fun hasStepCounterSensor(): Boolean {
        return stepCounterSensor != null
    }

    fun getStepsAccumulatedToday(): Float {
        return stepsAccumulatedToday
    }

    private fun saveStepCountState() {
        sharedPreferences.edit().apply {
            putFloat(KEY_INITIAL_SENSOR_READING_AT_START_OF_DAY, initialSensorReadingAtStartOfDay)
            putFloat(KEY_STEPS_ACCUMULATED_TODAY, stepsAccumulatedToday)
            apply()
        }
        println("ManejoContadorPasos: Estado de pasos guardado. InitialSensorReading: $initialSensorReadingAtStartOfDay, StepsAccumulatedToday: $stepsAccumulatedToday")
    }

    private fun loadStepCountState() {
        // Cargar los valores guardados
        initialSensorReadingAtStartOfDay = sharedPreferences.getFloat(KEY_INITIAL_SENSOR_READING_AT_START_OF_DAY, 0f)
        stepsAccumulatedToday = sharedPreferences.getFloat(KEY_STEPS_ACCUMULATED_TODAY, 0f)
        println("ManejoContadorPasos: Estado de pasos cargado. InitialSensorReading: $initialSensorReadingAtStartOfDay, StepsAccumulatedToday: $stepsAccumulatedToday")
    }

    fun resetDailySteps() {
        sharedPreferences.edit().apply {
            putFloat(KEY_INITIAL_SENSOR_READING_AT_START_OF_DAY, 0f) // Reiniciar el conteo inicial
            putFloat(KEY_STEPS_ACCUMULATED_TODAY, 0f) // Reiniciar el último total del sensor
            putString(KEY_LAST_COUNT_DATE, "") // Reiniciar la fecha para que se detecte como nuevo día
            apply()
        }
        initialStepCount = 0f
        lastSensorTotalSteps = 0f
        isInitialCountSet = false
        onStepCountChanged?.invoke(0f)
        println("ManejoContadorPasos: Conteo diario reiniciado.")
    }
}
