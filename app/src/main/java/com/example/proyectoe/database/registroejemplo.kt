package com.example.proyectoe.database

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RegistroFormulario(
    onGuardar: (String, String) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var carrera by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") }
        )
        OutlinedTextField(
            value = carrera,
            onValueChange = { carrera = it },
            label = { Text("Carrera") }
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            onGuardar(nombre, carrera)
        }) {
            Text("Guardar")
        }
    }
}