package com.example.proyectoe

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.proyectoe.data.repository.StepCounterRepository
import com.example.proyectoe.ui.dashboard.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class StepCounterService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var stepCounterSensor: Sensor? = null
    private lateinit var stepCounterRepository: StepCounterRepository
    private val NOTIFICATION_CHANNEL_ID = "step_counter_channel"
    private val NOTIFICATION_ID = 1

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(serviceJob)

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        // llama primero al servicio en segundo plano
        startForeground(NOTIFICATION_ID, buildNotification(0).build())

        // continua pidiendo el sensor
        stepCounterRepository = (application as MyApplication).stepCounterRepository
        stepCounterRepository.init()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        // si no encuentra el sensor ya no se cierra , solo lo para
        if (stepCounterSensor == null) {
            Log.e("StepService", "Step counter sensor not available on this device. Stopping service.")
            stopSelf()
            return
        }

        sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI)
        Log.d("StepService", "Sensor listener registered.")

        // observa los pasos y actualiza la noti
        serviceScope.launch {
            stepCounterRepository.currentDailySteps.collect { steps ->
                updateNotification(steps)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("StepService", "StepCounterService onStartCommand")
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val totalSensorSteps = event.values[0].toInt()
            // el repositorio maneja la lógica para calcular y guardar los pasos
            stepCounterRepository.updateTotalSteps(totalSensorSteps)
            // la notificación se actualizará automáticamente
            Log.d("StepService", "Sensor detectado: Steps totales: $totalSensorSteps, Daily steps: ${stepCounterRepository.currentDailySteps.value}")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No se usa
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("StepService", "StepCounterService onDestroy")
        sensorManager.unregisterListener(this)
        serviceJob.cancel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Contador de Pasos",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun buildNotification(steps: Int): NotificationCompat.Builder {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Contador de Pasos")
            .setContentText("Pasos hoy: $steps")
            .setSmallIcon(R.drawable.icono_pasos)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
    }

    private fun updateNotification(steps: Int) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = buildNotification(steps).build()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}