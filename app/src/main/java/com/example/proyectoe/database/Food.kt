package com.example.proyectoe.database

import com.google.firebase.firestore.DocumentId

data class FoodItem(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val calories: Float = 0f,
    val protein: Float = 0f,
    val fat: Float = 0f,
    val carbohydrates: Float = 0f,
    val details: String = "")