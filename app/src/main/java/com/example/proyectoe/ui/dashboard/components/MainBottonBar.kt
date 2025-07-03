package com.example.proyectoe.ui.dashboard.components

import android.R.attr.icon
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.example.proyectoe.R

@Composable
fun MainBottonBar(){
    NavigationBar(containerColor = colorResource(R.color.darkBlue)){
        val items = listOf(
            R.drawable.btn_1 to "Home",
            R.drawable.btn_2 to "Actividades",
            R.drawable.btn_3 to "Alimentos",
            R.drawable.btn_4 to "Perfil",
        )
        items.forEach { (icon,label)->
            NavigationBarItem(
                selected = false,
                onClick = {},
                icon = {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = label,
                        tint = Color.White
                    )
                },
                label = {
                    Text(text=label,color = Color.White, fontSize = 12.sp)
                }
            )
        }
    }
}