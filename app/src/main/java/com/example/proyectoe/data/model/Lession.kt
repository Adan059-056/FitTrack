package com.example.proyectoe.data.model

import java.io.Serializable


data class Lession(
    val title: String,
    val duration: String,
    val link: String,
    val picPath: String
) : Serializable
