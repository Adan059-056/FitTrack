package com.example.proyectoe.data.model

data class User(
    val uid: String = "",
    val nombre: String = "",
    val apellidos: String = "",
    val fechaNacimiento: String = "",
    val peso: String = "",
    val altura: String = "",
    val genero: String = "",
    val actividad: String = "",
    val objetivo: String = "",
    val email: String = "",
    val photoFileName: String? = null
    //val photoUrl: String? = null
)