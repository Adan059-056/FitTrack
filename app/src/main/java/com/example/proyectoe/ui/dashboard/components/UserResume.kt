package com.example.proyectoe.ui.dashboard.components
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Spacer
import com.example.proyectoe.ui.theme.CardColor
import com.example.proyectoe.ui.components.MyComposePieChart
private val BorderColor = Color(0xFF3A506B)

@Composable
fun UserResume() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            //.background(BackgroundColor)
            //Este padding agrega un espacio entre el texto Resumen y el card de circulo actividad
            .padding(top = 5.dp, bottom = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
        //verticalArrangement = Arrangement.spacedBy(5.dp) // Espaciado entre secciones
    ) {
        // Sección "Resumen"

        Text(
            "Resumen",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .padding(horizontal = 16.dp)

        )
    }
    UserResumeCard(
        title = "Circulo de Actividad",
        cardColor = CardColor,
        modifier = Modifier
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardColor),
            //horizontalAlignment = Alignment.CenterHorizontally
        ) {
            /*
            Text(
                text = "Circulo de Actividad", // Título para la gráfica
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(bottom = 12.dp)
            )*/
            Divider(
                color = BorderColor,
                thickness = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
            )
// Datos de ejemplo para la gráfica.
// **IMPORTANTE:** En una aplicación real, estos datos deberían ser dinámicos,
// obtenidos de un ViewModel o calculados.
            val weeklyData = mapOf(
                "Rutinas Completadas" to 8f,
                "Días de Descanso" to 2f,
                "Rutinas Pendientes" to 4f
            )
            MyComposePieChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp) // Dale una altura específica para que se muestre correctamente
                    .padding(horizontal = 16.dp), // Padding horizontal para la gráfica
                data = weeklyData,
//Text = "Actividad Semanal" // Texto central de la gráfica
            )
        }
    }

    Spacer(Modifier.height(16.dp))
}
