package com.example.proyectoe.database

    data class PasoDiario(
        val uid: String = "",
        val steps: Float = 0f,
        val date: String = "",
        val timestamp: com.google.firebase.Timestamp? = null
    )