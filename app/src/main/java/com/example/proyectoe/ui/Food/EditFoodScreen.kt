package com.example.proyectoe.ui.Food

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.proyectoe.database.FoodItem
import com.google.firebase.firestore.FirebaseFirestore

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditFoodScreen(
    foodItemId: String,
    onBack: () -> Unit = {},
    onFoodUpdated: () -> Unit = {}
) {
    val db = FirebaseFirestore.getInstance()
    var name by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    // Cargar datos del alimento
    LaunchedEffect(foodItemId) {
        db.collection("alimentos").document(foodItemId).get()
            .addOnSuccessListener { document ->
                document.toObject(FoodItem::class.java)?.let {
                    name = it.name
                    details = it.details
                    calories = it.calories
                }
                isLoading = false
            }
            .addOnFailureListener {
                errorMessage = "No se pudo cargar el alimento"
                isLoading = false
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Editar Alimento", style = MaterialTheme.typography.headlineSmall)

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = details,
                onValueChange = { details = it },
                label = { Text("Detalles") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = calories,
                onValueChange = { calories = it },
                label = { Text("Calorías") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Button(onClick = {
                val updatedFood = mapOf(
                    "name" to name,
                    "details" to details,
                    "calories" to calories
                )

                db.collection("alimentos").document(foodItemId)
                    .update(updatedFood)
                    .addOnSuccessListener {
                        successMessage = "Alimento actualizado exitosamente"
                        onFoodUpdated()
                    }
                    .addOnFailureListener {
                        errorMessage = "Error al actualizar"
                    }
            }) {
                Text("Guardar Cambios")
            }

            if (errorMessage != null) {
                Text(errorMessage!!, color = Color.Red)
            }
            if (successMessage != null) {
                Text(successMessage!!, color = Color.Green)
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Button(onClick = onBack) {
            Text("Volver")
        }
    }
}
