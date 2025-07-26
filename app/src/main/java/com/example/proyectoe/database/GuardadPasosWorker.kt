package com.example.proyectoe.database

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class GuardarPasosWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        val uid = auth.currentUser?.uid ?: return Result.failure()

        val sharedPrefs = applicationContext.getSharedPreferences("step_counter_prefs", Context.MODE_PRIVATE)
        val pasos = sharedPrefs.getFloat("steps_accumulated_today", 0f)
        val fecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val datos = hashMapOf(
            "uid" to uid,
            "steps" to pasos,
            "date" to fecha,
            "timestamp" to FieldValue.serverTimestamp()
        )

        return try {
            // Puedes cambiar esta colección si quieres hacerlo por usuario
            firestore.collection("daily_steps").add(datos).await()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
