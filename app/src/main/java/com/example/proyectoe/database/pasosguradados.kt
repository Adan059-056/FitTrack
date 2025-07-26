package com.example.proyectoe.database
import com.google.firebase.Timestamp


data class PasoDiario(
    val userId: String = "",
    val steps: Float = 0f,
    val date: String = "",
    val timestamp: com.google.firebase.Timestamp? = null
)