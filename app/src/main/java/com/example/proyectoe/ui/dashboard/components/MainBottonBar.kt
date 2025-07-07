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
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.proyectoe.R

@Composable
fun MainBottonBar(
    navController: NavController,  // Añadimos el NavController
    currentRoute: String?           // Añadimos la ruta actual
){
    // Definimos las rutas correspondientes a cada ítem
    val routes = listOf(
        "home",        // Para el primer ítem (Home)
        "favorites",  // Para el segundo ítem (Actividades)
        "food",        // Para el tercer ítem (Alimentos)
        "profile"      // Para el cuarto ítem (Perfil)
    )
    NavigationBar(containerColor = colorResource(R.color.darkBlue)){
        val items = listOf(
            R.drawable.btn_1 to "Home",
            R.drawable.btn_2 to "Favorites",
            R.drawable.btn_3 to "Alimentos",
            R.drawable.btn_4 to "Perfil",
        )
        items.forEachIndexed { index, (icon,label)->
            NavigationBarItem(
                // Marcamos como seleccionado si la ruta actual coincide
                selected = currentRoute == routes[index],
                onClick = {
                    // Navegamos a la ruta correspondiente
                    navController.navigate(routes[index]) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
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