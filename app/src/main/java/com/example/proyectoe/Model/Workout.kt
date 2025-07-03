package com.example.proyectoe.Model

import java.io.Serializable

data class Workout(
    val title: String,
    val description: String,
    val picPath: String,
    val kcal: Int,
    val durationAll: String,
    val lessions: List<Lession>
): Serializable
