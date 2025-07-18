package com.example.proyectoe.ui.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectoe.R
import com.example.proyectoe.ui.components.MyComposePieChart
import androidx.compose.foundation.background
import com.example.proyectoe.ui.theme.CardColor

@Composable
fun MonitoringSection()
{
    Column (modifier = Modifier.padding(vertical = 16.dp)){
        Text(
            text = "Datos Del Día",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 16.dp)
        )
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 44.dp),
                horizontalArrangement = Arrangement.SpaceBetween
        ){
            // Se muestran datos del día
            MonitorItem(R.drawable.monitor1, "2500", "Calorias Diarias")
            MonitorItem(R.drawable.monitor2, "6h 45min", "Horas Dormidas")
            MonitorItem(R.drawable.monitor3, "2w 4days", "Haciendo Ejercicio")


        }


    Spacer(Modifier.height(24.dp)) // Espacio antes de la gráfica

// --- Fin de la Gráfica de Pastel ---
}
}
@Composable
fun MonitorItem(icon:Int,value: String,label:String)
{
    Column (horizontalAlignment = Alignment.CenterHorizontally){
        Image(painter = painterResource(id = icon),
            contentDescription = label
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 4.dp)
        )
        Text(
            text = label,
            color = Color.White,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}