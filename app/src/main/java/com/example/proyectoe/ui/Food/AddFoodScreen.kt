package com.example.proyectoe.ui.Food


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.ui.graphics.Color

@Composable
fun AddFoodScreen(onBack: () -> Unit = {}) {
    var name by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Agregar Alimento", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = details,
            onValueChange = { details = it },
            label = { Text("Detalle") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = calories,
            onValueChange = { calories = it },
            label = { Text("Calorías") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val db = FirebaseFirestore.getInstance()
            val alimento = hashMapOf(
                "name" to name,
                "details" to details,
                "calories" to calories
            )

            db.collection("alimentos")
                .add(alimento)
                .addOnSuccessListener {
                    mensaje = "Alimento agregado exitosamente"
                    name = ""; details = ""; calories = ""
                }
                .addOnFailureListener {
                    mensaje = "Error al guardar alimento"
                }
        }) {
            Text("Guardar")
        }

        if (mensaje.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(mensaje, color = if ("exitosamente" in mensaje) Color.Green else Color.Red)
        }
    }
}
