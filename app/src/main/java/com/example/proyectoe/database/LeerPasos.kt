package com.example.proyectoe.database

import com.example.proyectoe.database.PasoDiario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PasoRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun obtenerPasosDelUsuario(): List<PasoDiario> {
        val uid = auth.currentUser?.uid ?: return emptyList()

        return try {
            val querySnapshot = firestore
                .collection("daily_steps")
                .whereEqualTo("userId", uid)
                .orderBy("date")
                .get()
                .await()

            querySnapshot.documents.mapNotNull { it.toObject(PasoDiario::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
