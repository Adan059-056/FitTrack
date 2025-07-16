package com.example.proyectoe.ui.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyectoe.data.model.Workout
import com.example.proyectoe.ui.dashboard.components.SearchBar
import com.example.proyectoe.ui.dashboard.components.OtherWorkoutsHeader
import com.example.proyectoe.ui.dashboard.components.Header
import com.example.proyectoe.ui.dashboard.components.BannerCard

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import android.app.Application // Para el factory de AndroidViewModel
import androidx.compose.runtime.LaunchedEffect
import com.example.proyectoe.ui.Profile.ProfileViewModel


@Composable
fun MainContent(
    modifier:Modifier = Modifier,
    workouts: List <Workout>, // Este parámetro ya lo tenías
    // AÑADE LA INYECCIÓN DE PROFILEVIEWMODEL AQUÍ
    profileViewModel: ProfileViewModel = run {
        val context = LocalContext.current
        val application = context.applicationContext as Application

        viewModel(
            factory = object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                    if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                        @Suppress("UNCHECKED_CAST")
                        return ProfileViewModel(application) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        )
    }
) {
    // OBSERVA LOS DATOS DEL USUARIO Y LA FOTO
    val user by profileViewModel.user.collectAsState()
    val profilePhotoUri by profileViewModel.profilePhotoUri.collectAsState()

    // Llama a loadUserProfile en el ViewModel cuando el Composable se inicializa
    LaunchedEffect(Unit) {
        profileViewModel.loadUserProfile()
    }
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())) {
        Spacer(Modifier.height(16.dp))
        Header(userName = user?.nombre ?: "Usuario",
            profilePhotoUri = profilePhotoUri)
        SearchBar()
        MonitoringSection()
        Spacer(Modifier.height(16.dp))
        BannerCard()
        Spacer(Modifier.height(16.dp))
        OtherWorkoutsHeader()
        Spacer(Modifier.height(16.dp))

        WorkOutList(workouts)
        Spacer(Modifier.height(16.dp))
    }
}