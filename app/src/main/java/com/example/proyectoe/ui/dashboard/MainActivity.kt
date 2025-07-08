package com.example.proyectoe.ui.dashboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.google.firebase.FirebaseApp
import com.example.proyectoe.ui.auth.RegisterScreen // Importa RegisterScreen
import com.example.proyectoe.ui.auth.SignInScreen // **Necesitarás crear esta pantalla de Sign In**
import com.google.firebase.auth.FirebaseAuth // Para verificar el estado de autenticación
import com.example.proyectoe.ui.auth.RegisterScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
// Inicializa Firebase al inicio de la Activity
        FirebaseApp.initializeApp(this)

        setContent{
            val navController = rememberNavController()
            val auth = FirebaseAuth.getInstance()// Obtén la instancia de Firebase Auth

            // Determina la ruta de inicio basada en si el usuario está autenticado
            val startDestination = if (auth.currentUser != null) {
                "home" // Si hay un usuario logueado, ve a la pantalla principal
            } else {
                "singin_route" // Si no hay usuario, ve a la pantalla de inicio de sesión
            }
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val showBottomBar = remember(currentRoute) { // Usa currentRoute como clave para remember
                currentRoute !in listOf("singin_route", "register_route")
            }

    /*private val workouts = getData()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FirebaseApp.initializeApp(this)
            // 1. Crea el controlador de navegación
            val workouts by remember { mutableStateOf(getData()) }

            //NavActivitie
            val navController = rememberNavController()

            // 2. Obtiene la ruta actual para resaltar el ítem activo
            val currentRoute = remember {
                derivedStateOf {
                    navController.currentBackStackEntry?.destination?.route ?: "home"
                }
            }.value
*/
            MaterialTheme {
                Scaffold(
                    containerColor = Color(0Xff101322),
                    bottomBar = {
                        if (showBottomBar) { // Muestra la barra inferior solo si no estás en auth

                            // 3. Pasamos el navController y la ruta actual
                            MainBottonBar(
                                navController = navController,
                                currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                                    ?: "home"
                            )
                        }
                    }
                ) { innerPadding ->
                    // 4. Configuración del sistema de navegación
                    NavHost(
                        navController = navController,
                        startDestination = startDestination, // Ruta de inicio dinámica,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // Rutas de autenticación
                        composable("singin_route") {
                            // **Aquí pasarás tu IntroFooter con la navegación a Register**
                            SignInScreen(
                                onSignInSuccess = {
                                    // Si el login es exitoso, navega a la pantalla principal y limpia el back stack
                                    navController.navigate("home") {
                                        popUpTo("singin_route") { inclusive = true } // Elimina las pantallas de auth
                                    }
                                },
                                onNavigateToRegister = {
                                    // Navega a la pantalla de registro
                                    navController.navigate("register_route")
                                }
                            )
                        }
                        composable("register_route") {
                            RegisterScreen(
                                onRegisterSuccess = {
                                    // Si el registro es exitoso, navega a la pantalla principal y limpia el back stack
                                    navController.navigate("home") {
                                        popUpTo("singin_route") { inclusive = true } // Elimina las pantallas de auth
                                    }
                                },
                                onNavigateBack = {
                                    // Vuelve a la pantalla de inicio de sesión
                                    navController.popBackStack()
                                }
                            )
                        }

                        // Pantalla de Inicio
                        composable("home") {
                            val workouts = getData()
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
                            ProfileScreen(
                                onBack = { navController.popBackStack() },
                                onLogout = {
                                    auth.signOut() // Cerrar sesión en Firebase
                                    navController.navigate("singin_route") {
                                        popUpTo("home") { inclusive = true } // Elimina el back stack de la app
                                    }
                                }
                            )
                        }
                    }
                    }
                    }
            }
        }
    }