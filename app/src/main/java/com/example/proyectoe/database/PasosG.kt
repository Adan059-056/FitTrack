package com.example.proyectoe.database
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectoe.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class StepCounterActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var stepCounter: Sensor? = null
    private var totalSteps = 0f
    private var previousTotalSteps = 0f

    private lateinit var stepCountTextView: TextView
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        stepCountTextView = findViewById(R.id.main)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        stepCounter?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            val steps = event.values[0]
            val currentSteps = steps - previousTotalSteps
            totalSteps = currentSteps

            stepCountTextView.text = "Pasos: ${totalSteps.toInt()}"

            // Guardar localmente en SharedPreferences
            val prefs = getSharedPreferences("step_prefs", MODE_PRIVATE)
            prefs.edit().putFloat("daily_steps", totalSteps).apply()
        }
    }

    private fun saveStepsToFirestore(steps: Int) {
        val user = auth.currentUser
        if (user == null) {
            println("Usuario no autenticado, no se guardan los pasos")
            return
        }

        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val stepsData = hashMapOf(
            "fecha" to date,
            "pasos" to steps
        )

        firestore.collection("users")
            .document(user.uid)
            .collection("steps")
            .document(date)
            .set(stepsData)
            .addOnSuccessListener {
                println("Pasos guardados en Firestore para el usuario ${user.uid}: $steps")
            }
            .addOnFailureListener { e ->
                println("Error al guardar pasos en Firestore: ${e.message}")
            }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }
}
