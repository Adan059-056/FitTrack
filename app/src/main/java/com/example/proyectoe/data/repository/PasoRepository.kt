package com.example.proyectoe.data.repository

import com.example.proyectoe.data.model.PasoDiario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/*
    es un repositorio que se encarga de obtener datos de pasos diarios desde Firebase Firestore.
 */
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
