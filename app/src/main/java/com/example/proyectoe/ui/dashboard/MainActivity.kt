package com.example.proyectoe.ui.dashboard

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.navigation.compose.*
import com.example.proyectoe.data.datasource.WorkoutDataProvider.getData
import com.example.proyectoe.database.StartScreen
import com.example.proyectoe.ui.Favorites.FavoritesScreen
import com.example.proyectoe.ui.Food.AddFoodScreen
import com.example.proyectoe.ui.Food.FoodScreen
import com.example.proyectoe.ui.Profile.ProfileScreen
import com.example.proyectoe.ui.auth.RegisterScreen
import com.example.proyectoe.ui.auth.SignInScreen
import com.example.proyectoe.ui.dashboard.components.MainBottonBar
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

import com.example.proyectoe.ui.Food.EditFoodScreen
import androidx.navigation.navArgument
import androidx.navigation.NavType

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            println("Permiso ACTIVITY_RECOGNITION concedido!")
        } else {
            println("Permiso ACTIVITY_RECOGNITION denegado. El contador de pasos no funcionará.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)
        checkAndRequestActivityRecognitionPermission()

        setContent {
            val navController = rememberNavController()
            val auth = FirebaseAuth.getInstance()

            // Determine the start destination based on authentication state
            val startDestination = if (auth.currentUser != null) {
                "home"
            } else {
                "singin_route"
            }

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            // Hide bottom bar for specific routes
            val showBottomBar = currentRoute !in listOf("start", "singin_route", "register_route", "add_food_route")

            MaterialTheme  {
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
                        startDestination = startDestination, // Use dynamic start destination
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("start") {
                            StartScreen(
                                        onUserLoggedIn = {
                                    navController.navigate("home") {
                                        popUpTo("start") { inclusive = true }
                                        launchSingleTop = true
                                    }
                                },
                                onUserNotLoggedIn = {
                                    navController.navigate("singin_route") {
                                        popUpTo("start") { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }

                        composable("singin_route") {
                            SignInScreen(
                                onSignInSuccess = {
                                    navController.navigate("home") {
                                        popUpTo("singin_route") { inclusive = true }
                                        launchSingleTop = true
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
                                        launchSingleTop = true
                                    }
                                },
                                onNavigateBack = {
                                    navController.navigate("singin_route") {
                                        popUpTo("register_route") { inclusive = true }
                                        launchSingleTop = true
                                    }
                                },
                                onNavigateIntro = {
                                    finish()
                                }
                            )
                        }

                        composable("home") {
                            val workouts = getData()
                            MainContent(workouts = workouts)
                        }
                        composable("favorites") {
                            FavoritesScreen()
                        }
                        composable("food") {
                            FoodScreen(
                                onBack = { navController.popBackStack() },
                                onAddFood = { navController.navigate("add_food_route") },
                                onEditFood = { foodId -> navController.navigate("edit_food_route/$foodId") }
                            )
                        }
                        composable("add_food_route") {
                            AddFoodScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable(
                            route = "edit_food_route/{foodId}", // Define la ruta con el argumento
                            arguments = listOf(navArgument("foodId") { type = NavType.StringType }) // Especifica el tipo
                        ) { backStackEntry ->
                            val foodId = backStackEntry.arguments?.getString("foodId")
                            if (foodId != null) {
                                EditFoodScreen(
                                    foodItemId = foodId,
                                    onBack = { navController.popBackStack() },
                                    onFoodUpdated = {
                                        // Después de actualizar, puedes volver a la pantalla de alimentos
                                        navController.popBackStack()
                                    }
                                )
                            } else {
                                // Manejar el caso de ID nulo, quizás mostrar un Toast o un log
                                Toast.makeText(this@MainActivity, "Error: ID de alimento no proporcionado", Toast.LENGTH_SHORT).show()
                                navController.popBackStack() // Regresar para evitar un estado roto
                            }
                        }
                        composable("profile") {
                            ProfileScreen(
                                onBack = { navController.popBackStack() },
                                onLogout = {
                                    auth.signOut()
                                    navController.navigate("singin_route") {
                                        popUpTo("home") { inclusive = true }
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

    private fun checkAndRequestActivityRecognitionPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED -> {
                println("Permiso ACTIVITY_RECOGNITION ya concedido.")
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACTIVITY_RECOGNITION) -> {
                println("Necesitamos el permiso de reconocimiento de actividad para contar tus pasos. Por favor, concédelo.")
                requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
            }
        }
    }
}