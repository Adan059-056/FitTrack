package com.example.proyectoe.data.datasource.notificaciones

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.proyectoe.R

class NotificationReceiver : BroadcastReceiver() {

    private val CHANNEL_ID = "notification_channel_food"
    private val NOTIFICATION_ID = 101 // ID único para las notificaciones

    override fun onReceive(context: Context, intent: Intent) {
        val message = intent.getStringExtra("NOTIFICATION_MESSAGE") ?: "Es hora de registrar tus alimentos."

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crea el canal de notificación
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Recordatorios de Alimentación",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Recordatorios para registrar tus alimentos diarios."
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Construye la notificación
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Recordatorio de Alimentación")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        // Muestra la notificación
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }
}