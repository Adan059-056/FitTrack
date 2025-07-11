package com.example.proyectoe.ui.dashboard


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.*
import com.example.proyectoe.data.datasource.WorkoutDataProvider.getData
import com.example.proyectoe.database.StartScreen
import com.example.proyectoe.ui.Favorites.FavoritesScreen
import com.example.proyectoe.ui.Food.FoodScreen
import com.example.proyectoe.ui.Profile.ProfileScreen
import com.example.proyectoe.ui.auth.RegisterScreen
import com.example.proyectoe.ui.auth.SignInScreen
import com.example.proyectoe.ui.dashboard.components.MainBottonBar
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa Firebase. Es crucial que esto se haga una vez al inicio de la aplicación.
        // Se recomienda hacerlo en una clase Application personalizada para una mejor gestión del ciclo de vida.
        FirebaseApp.initializeApp(this)

        setContent {
            // rememberNavController() crea y recuerda un NavController para la navegación.
            val navController = rememberNavController()
            // Obtiene una instancia de FirebaseAuth para gestionar la autenticación.
            val auth = FirebaseAuth.getInstance()

            // Observa el estado actual de la pila de navegación para determinar la ruta actual.
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            // Define si la barra de navegación inferior debe mostrarse.
            // No se mostrará en las pantallas de inicio, inicio de sesión o registro.
            val showBottomBar = currentRoute !in listOf("start", "singin_route", "register_route")

            // MaterialTheme proporciona estilos y colores por defecto para los componentes de Material Design.
            MaterialTheme {
                // Scaffold proporciona la estructura básica de la pantalla (barra superior, barra inferior, etc.).
                Scaffold(
                    // Establece el color de fondo del Scaffold.
                    containerColor = Color(0xFF101322),
                    // Define la barra de navegación inferior si showBottomBar es true.
                    bottomBar = {
                        if (showBottomBar) {
                            MainBottonBar(
                                navController = navController,
                                currentRoute = currentRoute ?: "home" // Valor por defecto si currentRoute es nulo
                            )
                        }
                    }
                ) { innerPadding ->
                    // NavHost es el contenedor para todas las rutas de navegación.
                    NavHost(
                        navController = navController,
                        // La ruta de inicio predeterminada es "start", que maneja la verificación de sesión.
                        startDestination = "start",
                        // Aplica el padding necesario para que el contenido no se superponga con la barra inferior.
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // Definición de la ruta "start" (Splash o verificación de sesión).
                        composable("start") {
                            // StartScreen es un Composable que verifica si el usuario ya está logueado.
                            StartScreen(
                                // Callback cuando el usuario ya ha iniciado sesión.
                                onUserLoggedIn = {
                                    // Navega a la pantalla "home" y limpia la pila de navegación.
                                    navController.navigate("home") {
                                        popUpTo("start") { inclusive = true } // Elimina "start" de la pila
                                        launchSingleTop = true // Evita múltiples instancias de "home"
                                    }
                                },
                                // Callback cuando el usuario no ha iniciado sesión.
                                onUserNotLoggedIn = {
                                    // Navega a la pantalla de inicio de sesión y limpia la pila de navegación.
                                    navController.navigate("singin_route") {
                                        popUpTo("start") { inclusive = true } // Elimina "start" de la pila
                                        launchSingleTop = true // Evita múltiples instancias de "singin_route"
                                    }
                                }
                            )
                        }

                        // Definición de la ruta "singin_route" (Inicio de sesión).
                        composable("singin_route") {
                            SignInScreen(
                                // Callback cuando el inicio de sesión es exitoso.
                                onSignInSuccess = {
                                    // Navega a "home" y limpia la pila de navegación.
                                    navController.navigate("home") {
                                        popUpTo("singin_route") { inclusive = true }
                                        launchSingleTop = true
                                    }
                                },
                                // Callback para navegar a la pantalla de registro.
                                onNavigateToRegister = {
                                    navController.navigate("register_route")
                                }
                            )
                        }

                        // Definición de la ruta "register_route" (Registro).
                        composable("register_route") {
                            RegisterScreen(
                                // Callback cuando el registro es exitoso.
                                onRegisterSuccess = {
                                    // Navega a "home" y limpia la pila de navegación.
                                    navController.navigate("home") {
                                        popUpTo("register_route") { inclusive = true }
                                        launchSingleTop = true
                                    }
                                },
                                // Callback para volver atrás (ej. finalizar la actividad si es la única en la pila).
                                onNavigateBack = {
                                    finish()
                                },
                                // Callback para navegar a la introducción (similar a back o finish en este contexto).
                                onNavigateIntro = {
                                    finish()
                                }
                            )
                        }

                        // Definición de la ruta "home" (Pantalla principal/Dashboard).
                        composable("home") {
                            // Obtiene los datos de los entrenamientos.
                            val workouts = getData()
                            // Muestra el contenido principal de la aplicación.
                            MainContent(workouts = workouts)
                        }
                        // Definición de la ruta "favorites".
                        composable("favorites") {
                            FavoritesScreen()
                        }
                        // Definición de la ruta "food".
                        composable("food") {
                            FoodScreen()
                        }
                        // Definición de la ruta "profile".
                        composable("profile") {
                            ProfileScreen(
                                // Callback para volver atrás en la navegación.
                                onBack = { navController.popBackStack() },
                                // Callback para cerrar sesión.
                                onLogout = {
                                    auth.signOut() // Cierra la sesión de Firebase.
                                    // Navega a la pantalla de inicio de sesión y limpia la pila de navegación.
                                    navController.navigate("singin_route") {
                                        popUpTo("home") { inclusive = true } // Elimina "home" y anteriores
                                        launchSingleTop = true
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