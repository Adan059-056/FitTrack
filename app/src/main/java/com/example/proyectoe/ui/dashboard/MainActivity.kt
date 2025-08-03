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
import com.example.proyectoe.ui.StartScreen
import com.example.proyectoe.ui.Favorites.FavoritesScreen
import com.example.proyectoe.ui.Food.AddFoodScreen
import com.example.proyectoe.ui.Food.FoodScreen
import com.example.proyectoe.ui.Profile.ProfileScreen
import com.example.proyectoe.ui.auth.RegisterScreen
import com.example.proyectoe.ui.auth.LoginScreen
import com.example.proyectoe.ui.dashboard.components.MainBottonBar
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.example.proyectoe.ui.Food.EditFoodScreen
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.proyectoe.ui.auth.LoginActivity
import android.os.Build
import com.example.proyectoe.StepCounterService

class MainActivity : ComponentActivity() {

    private val requestMultiplePermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        var activityRecognitionGranted = false
        permissions.entries.forEach {
            val permissionName = it.key
            val isGranted = it.value

            when (permissionName) {
                Manifest.permission.ACTIVITY_RECOGNITION -> {
                    if (isGranted) {
                        println("Permiso ACTIVITY_RECOGNITION concedido.")
                        activityRecognitionGranted = true
                    } else {
                        println("Permiso ACTIVITY_RECOGNITION denegado.")
                    }
                }

                Manifest.permission.POST_NOTIFICATIONS -> {
                    if (isGranted) {
                        println("Permiso POST_NOTIFICATIONS concedido.")
                    } else {
                        println("Permiso POST_NOTIFICATIONS denegado.")
                    }
                }
            }
        }
        if (activityRecognitionGranted) {
            startStepCounterService()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)
        checkAndRequestAllPermissions()

        setContent {
            val navController = rememberNavController()
            val auth = FirebaseAuth.getInstance()

            // Da la ruta despues de pasar la autenticacion o si no funciona
            val startDestination = if (auth.currentUser != null) {
                "home"
            } else {
                "singin_route"
            }

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            // para esconder la barra de botones o mostralos
            val showBottomBar =
                currentRoute !in listOf("start", "singin_route", "register_route", "add_food_route")

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
                        startDestination = startDestination,
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
                                    val intent =
                                        Intent(this@MainActivity, LoginActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            )
                        }

                        composable("singin_route") {
                            val intent = Intent(this@MainActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
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
                                    val intent =
                                        Intent(this@MainActivity, LoginActivity::class.java)
                                    startActivity(intent)
                                    finish()
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
                            arguments = listOf(navArgument("foodId") {
                                type = NavType.StringType
                            }) // Especifica el tipo
                        ) { backStackEntry ->
                            val foodId = backStackEntry.arguments?.getString("foodId")
                            if (foodId != null) {
                                EditFoodScreen(
                                    foodItemId = foodId,
                                    onBack = { navController.popBackStack() },
                                    onFoodUpdated = {
                                        // Después de actualizar para volver a la pantalla de alimentos
                                        navController.popBackStack()
                                    }
                                )
                            } else {
                                // Manejar el caso de ID nulo
                                Toast.makeText(
                                    this@MainActivity,
                                    "Error: ID de alimento no proporcionado",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.popBackStack() // Regresa para evitar un estado roto
                            }
                        }
                        composable("profile") {
                            ProfileScreen(
                                onBack = { navController.popBackStack() },
                                onLogout = {
                                    auth.signOut()
                                    val intent =
                                        Intent(this@MainActivity, LoginActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun checkAndRequestAllPermissions() {
        val permissionsToRequest = mutableListOf<String>()
        var activityRecognitionGranted = false

        // Lógica para ACTIVITY_RECOGNITION
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.ACTIVITY_RECOGNITION)
            } else {
                activityRecognitionGranted = true
            }
        } else {
            // Para versiones de Android < Q, se asume que el permiso está concedido en el manifest
            activityRecognitionGranted = true
        }

        // Lógica para POST_NOTIFICATIONS
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestMultiplePermissionsLauncher.launch(permissionsToRequest.toTypedArray())
        } else if (activityRecognitionGranted) {
            // Inicia el servicio solo si no se necesitan permisos adicionales
            startStepCounterService()
        }
    }

    private fun startStepCounterService() {
        val serviceIntent = Intent(this, StepCounterService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }
}