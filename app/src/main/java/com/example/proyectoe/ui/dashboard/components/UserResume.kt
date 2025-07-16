package com.example.proyectoe.ui.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column // Importar Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectoe.ui.components.MyComposePieChart
import com.example.proyectoe.ui.theme.BackgroundColor // Asumiendo que está definida

@Composable
fun UserResume() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(24.dp), // Aplicar el padding directamente al LazyColumn
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp) // Espaciado entre secciones
    ) {
        // Sección "Resumen"
        item {
            Text(
                "Resumen",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                // No se necesita constrainAs aquí, LazyColumn maneja la disposición vertical
                //modifier = Modifier.fillMaxWidth() // Llenar el ancho para un mejor control de centrado
            )
        }

        // Sección "Círculo de Actividad" y Gráfico de Pastel
        item {
            Column( // Usar Column para organizar el título y el gráfico verticalmente dentro de este elemento
                horizontalAlignment = Alignment.CenterHorizontally,
                //modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Círculo de Actividad", // Título para el gráfico
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 12.dp) // Añadir padding debajo del título
                )

                val weeklyData = mapOf(
                    "Rutinas Completadas" to 8f,
                    "Días de Descanso" to 2f,
                    "Rutinas Pendientes" to 4f
                )

                MyComposePieChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp) // Dar una altura específica
                        .padding(horizontal = 16.dp), // Padding horizontal para el gráfico
                    data = weeklyData,
                    // Text = "Actividad Semanal" // Si MyComposePieChart soporta un texto central
                )
            }
        }

        // Puedes añadir más bloques `item { ... }` aquí para contenido adicional
        // Por ejemplo:
        // item {
        //     Text("Otra sección", color = Color.White, fontSize = 18.sp, modifier = Modifier.padding(top = 16.dp))
        // }
    }
}