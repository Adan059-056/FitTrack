package com.example.proyectoe.ui.dashboard.components
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
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
import com.example.proyectoe.ui.common.components.MyComposePieChart
import androidx.compose.foundation.layout.Row
import com.github.mikephil.charting.components.Legend

private val BorderColor = Color(0xFF3A506B)

@Composable
fun UserResume(currentSteps: Int, distanceKm: Float) {
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
    ) {
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

            //valores de la grafica
            val dailyStepGoal = 500f // meta de pasos
            val currentStepsFloat = currentSteps.toFloat()
            val completedSteps = if (currentStepsFloat > dailyStepGoal) dailyStepGoal else currentStepsFloat
            val remainingSteps = dailyStepGoal - completedSteps

            // colores de la grafica
            val pieChartSegmentColors = ArrayList<Int>()
            val completedColorInt = android.graphics.Color.parseColor("#FF3B3B") // rojo
            val remainingColorInt = android.graphics.Color.parseColor("#50000066") // naranja
            val finalColorInt = android.graphics.Color.parseColor("#31A84F") // verde

            val pieChartData = mutableMapOf<String, Float>()


            // como se comportan los colores
            if (completedSteps >= dailyStepGoal) {
                pieChartSegmentColors.add(finalColorInt)
                pieChartData["Completados"] = dailyStepGoal
            } else if (currentStepsFloat == 0f && dailyStepGoal > 0f) {
                pieChartSegmentColors.add(remainingColorInt)
                pieChartData["Restantes"] = dailyStepGoal
            } else {
                pieChartSegmentColors.add(completedColorInt)
                pieChartSegmentColors.add(remainingColorInt)

                pieChartData["Completados"] = completedSteps
                pieChartData["Restantes"] = remainingSteps
            }


            val legendEntries = ArrayList<com.github.mikephil.charting.components.LegendEntry>()

            //agrega los textos debajo de la grafica
            legendEntries.add(
                com.github.mikephil.charting.components.LegendEntry(
                    "Objetivo ${dailyStepGoal.toInt()} pasos",
                    Legend.LegendForm.CIRCLE,
                    10f,//tamaño
                    0f,
                    null,
                    remainingColorInt //color
                )
            )

            legendEntries.add(
                com.github.mikephil.charting.components.LegendEntry(
                    "Pasos actuales $currentSteps",
                    Legend.LegendForm.CIRCLE,
                    10f,
                    0f,
                    null,
                    completedColorInt
                )
            )


            MyComposePieChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp) // tamaño de la grafica
                    .padding(horizontal = 16.dp), // Padding horizontal para la gráfica
                    data = pieChartData,
                    segmentColors = pieChartSegmentColors,
                    customLegendEntries = legendEntries
                //Text = "Actividad Semanal" // Texto central de la gráfica
            )
        }
    }

   Spacer(Modifier.height(16.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth(0.9f),
            //.padding(horizontal = 16.dp),
           // .background(CardColor),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        MiniStatCard(
            title = "Conteo",
            day = "Hoy",
            value = "$currentSteps Pasos",
            //unit = "Pasos",
            cardColor = CardColor,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)

        )
        MiniStatCard(
            title = "Distancia",
            day = "Hoy",
            value = "$distanceKm KM",
            //unit = "KM",
            cardColor = CardColor,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)

        )
    }
}




