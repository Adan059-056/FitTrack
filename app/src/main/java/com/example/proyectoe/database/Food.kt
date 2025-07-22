package com.example.proyectoe.database

import com.google.firebase.firestore.DocumentId

data class FoodItem(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val calories: Float = 0f, // Cambiado a Float
    val protein: Float = 0f,   // Cambiado a Float
    val fat: Float = 0f,      // Cambiado a Float
    val carbohydrates: Float = 0f, // Asegúrate de que este campo exista y sea Float
    val details: String = "") // Si quieres mantener el campo details en AddFoodScreen, debe estar aquí)
//){
//    constructor() : this("", "", "", "", "", "")
//}