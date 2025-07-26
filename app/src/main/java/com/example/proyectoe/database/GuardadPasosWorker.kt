package com.example.proyectoe.database

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class UploadStepsWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val prefs = applicationContext.getSharedPreferences("step_prefs", Context.MODE_PRIVATE)
        val steps = prefs.getFloat("daily_steps", 0f).toInt()

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            println("No se pudo guardar: usuario no autenticado")
            return Result.failure()
        }

        val firestore = FirebaseFirestore.getInstance()
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val data = hashMapOf("fecha" to date, "pasos" to steps)

        firestore.collection("users")
            .document(user.uid)
            .collection("steps")
            .document(date)
            .set(data)
            .addOnSuccessListener {
                println("Pasos diarios guardados correctamente")
            }
            .addOnFailureListener {
                println("Error al guardar pasos: ${it.message}")
            }

        return Result.success()
    }
}
