package com.example.proyectoe.data.model

/*
    es una clase de modelo de datos. Representa la estructura de los datos de pasos diarios
    tal como los almacenarías o recuperarías de una fuente, en este caso, Firebase Firestore.
 */
data class PasoDiario(
    val userId: String = "",
    val steps: Float = 0f,
    val date: String = "",
    val timestamp: com.google.firebase.Timestamp? = null
)