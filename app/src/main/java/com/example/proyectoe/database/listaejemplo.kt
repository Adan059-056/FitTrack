package com.example.proyectoe.database

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import com.example.proyectoe.database.ejemploC1
import com.google.firebase.firestore.FirebaseFirestore


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ListaEstudiantes() {
    val listaEstudiantes = remember { mutableStateListOf<ejemploC1>() }
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("estudiantes")
            .get()
            .addOnSuccessListener { result ->
                for (doc in result) {
                    listaEstudiantes.add(doc.toObject(ejemploC1::class.java))
                }
            }
    }

    Column {
        when {
            isLoading.value -> Text("Cargando estudiantes...")
            errorMessage.value != null -> Text("Error: ${errorMessage.value}")
            listaEstudiantes.isEmpty() -> Text("No se encontraron estudiantes.")
            else -> {
                LazyColumn {
                    items(listaEstudiantes) { estudiante ->
                        Column {
                            Text("Nombre: ${estudiante.nombre}")
                            Text("Carrera: ${estudiante.carrera}")
                            Divider()
                        }
                    }
                }
            }
        }
    }
}