package com.example.proyectoe.data.model

import com.google.firebase.firestore.DocumentId


// Aqui se define la estructura del elemento comida, que proviene de Firestore
data class FoodItem(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val calories: Float = 0f,
    val protein: Float = 0f,
    val fat: Float = 0f,
    val carbohydrates: Float = 0f,
    val details: String = "")