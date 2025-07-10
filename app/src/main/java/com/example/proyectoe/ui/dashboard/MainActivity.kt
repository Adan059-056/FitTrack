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
import com.example.proyectoe.database.StartScreen
import com.google.firebase.FirebaseApp
import com.example.proyectoe.ui.auth.RegisterScreen // Importa RegisterScreen
import com.example.proyectoe.ui.auth.SignInScreen // **Necesitarás crear esta pantalla de Sign In**
import com.google.firebase.auth.FirebaseAuth // Para verificar el estado de autenticación
import com.example.proyectoe.ui.auth.RegisterScreen
import com.example.proyectoe.ui.intro.IntroScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa Firebase
        FirebaseApp.initializeApp(this)

        setContent {
            val navController = rememberNavController()
            val auth = FirebaseAuth.getInstance()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            val showBottomBar = currentRoute !in listOf("start", "singin_route", "register_route")

            MaterialTheme {
                Scaffold(
                    containerColor = Color(0xFF101322),
                    bottomBar = {
                        if (showBottomBar) {
                            MainBottonBar(
                                navController = navController,
                                currentRoute = currentRoute ?: "home"
                            )
                        }
                    }
                ) { innerPadding ->

                    NavHost(
                        navController = navController,
                        startDestination = "start",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // ✅ Splash / Session check
                        composable("start") {
                            StartScreen(
                                onUserLoggedIn = {
                                    navController.navigate("home") {
                                        popUpTo("start") { inclusive = true }
                                    }
                                },
                                onUserNotLoggedIn = {
                                    navController.navigate("singin_route") {
                                        popUpTo("start") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // Autenticación
                        composable("singin_route") {
                            SignInScreen(
                                onSignInSuccess = {
                                    navController.navigate("home") {
                                        popUpTo("singin_route") { inclusive = true }
                                    }
                                },
                                onNavigateToRegister = {
                                    navController.navigate("register_route")
                                }
                            )
                        }

                        composable("register_route") {
                            RegisterScreen(
                                onRegisterSuccess = {
                                    navController.navigate("home") {
                                        popUpTo("register_route") { inclusive = true }
                                    }
                                },
                                onNavigateBack = {
                                    finish()
                                },
                                onNavigateIntro = {
                                    finish()
                                }
                            )
                        }

                        // App principal
                        composable("home") {
                            val workouts = getData()
                            MainContent(workouts = workouts)
                        }
                        composable("favorites") {
                            FavoritesScreen()
                        }
                        composable("food") {
                            FoodScreen()
                        }
                        composable("profile") {
                            ProfileScreen(
                                onBack = { navController.popBackStack() },
                                onLogout = {
                                    auth.signOut()
                                    navController.navigate("singin_route") {
                                        popUpTo("home") { inclusive = true }
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