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
import com.example.proyectoe.ui.dashboard.components.TextoEjercicio
import com.example.proyectoe.ui.dashboard.components.TextoMotivacion
import com.example.proyectoe.ui.dashboard.components.Header
import com.example.proyectoe.ui.dashboard.components.BannerClickleable

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import android.app.Application
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import com.example.proyectoe.ui.Profile.ProfileViewModel
import com.example.proyectoe.ui.dashboard.components.UserResume
import androidx.compose.foundation.layout.fillMaxSize
import com.example.proyectoe.ui.dashboard.components.UserResume
import androidx.compose.ui.Alignment
//import com.example.proyectoe.ui.dashboard.components.miniStatCard
import com.example.proyectoe.ui.dashboard.DashboardViewModel
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner

@Composable
fun MainContent(
    modifier: Modifier = Modifier,
    workouts: List<Workout>,
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
    },

    dashboardViewModel: DashboardViewModel = run {//metemos o inyectamos el viewmodel del dahsbord
        val context = LocalContext.current
        val application = context.applicationContext as Application

        viewModel(
            factory = object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                    if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
                        @Suppress("UNCHECKED_CAST")
                        return DashboardViewModel(application) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        )
    }

) {

    val user by profileViewModel.user.collectAsState()
    val profilePhotoUri by profileViewModel.profilePhotoUri.collectAsState()

    LaunchedEffect(Unit) {
        profileViewModel.loadUserProfile()
    }

    val steps by dashboardViewModel.currentSteps.observeAsState(0f)
    val distance by dashboardViewModel.distanceKm.observeAsState(0f)
    val lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(Modifier.height(16.dp))
        }
        item {
            Header(
                userName = user?.nombre ?: "Usuario",
                profilePhotoUri = profilePhotoUri
            )
            //SearchBar()
            UserResume(currentSteps = steps.toInt(),distanceKm = distance) // se pasan los pasos xd
            Spacer(Modifier.height(16.dp))
            TextoMotivacion()
            Spacer(Modifier.height(16.dp))
            BannerClickleable()
            Spacer(Modifier.height(16.dp))
            TextoEjercicio()
            Spacer(Modifier.height(16.dp))
            WorkOutList(workouts)
            Spacer(Modifier.height(16.dp))
        }
    }
}