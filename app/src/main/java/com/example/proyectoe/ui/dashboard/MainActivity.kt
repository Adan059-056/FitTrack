package com.example.proyectoe.ui.dashboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.proyectoe.data.datasource.WorkoutDataProvider.getData
import com.example.proyectoe.ui.Favorites.FavoritesScreen
import com.example.proyectoe.ui.Food.FoodScreen
import com.example.proyectoe.ui.Profile.ProfileScreen
import com.example.proyectoe.ui.dashboard.components.MainBottonBar
import com.example.proyectoe.ui.dashboard.MainContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.proyectoe.data.model.Workout

class MainActivity : ComponentActivity() {
    private val workouts = getData()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // 1. Crea el controlador de navegación
            val workouts by remember { mutableStateOf(getData()) }

            val navController = rememberNavController()

            // 2. Obtiene la ruta actual para resaltar el ítem activo
            val currentRoute = remember {
                derivedStateOf {
                    navController.currentBackStackEntry?.destination?.route ?: "home"
                }
            }.value

            MaterialTheme {
                Scaffold(
                    containerColor = Color(0Xff101322),
                    bottomBar = {
                        // 3. Pasamos el navController y la ruta actual
                        MainBottonBar(
                            navController = navController,
                            currentRoute = currentRoute
                        )
                    }
                ) { innerPadding ->
                    // 4. Configuración del sistema de navegación
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // Pantalla de Inicio
                        composable("home") {
                            MainContent(workouts = workouts)
                        }
                        // Pantalla de Favoritos
                        composable("favorites") {
                            FavoritesScreen()
                        }
                        // Pantalla de Alimentos
                        composable("food") {
                            FoodScreen()
                        }
                        // Pantalla de Perfil
                        composable("profile") {
                            ProfileScreen()
                        }
                        //Agregado
                        composable("profile") {
                            ProfileScreen(
                                onBack = { navController.popBackStack() },
                                onLogout = { /* Lógica de cierre de sesión */ }
                            )
                        }
                    }
                }
            }
        }
    }
}