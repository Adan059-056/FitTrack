package com.example.proyectoe.database

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

fun guardarejemplo(estudiente: ejemploC1){
    val db = FirebaseFirestore.getInstance()
    db.collection("estudiantes")
        .add(estudiente)
        .addOnSuccessListener { Log.d("Firebase","Guardado") }
        .addOnFailureListener { e -> Log.e ("Firebase","Error:e")}
}