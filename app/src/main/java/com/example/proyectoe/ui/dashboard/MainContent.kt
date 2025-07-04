package com.example.proyectoe.ui.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
//import androidx.compose.material3.SearchBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyectoe.data.model.Workout
import com.example.proyectoe.ui.dashboard.components.SearchBar
import com.example.proyectoe.ui.dashboard.components.OtherWorkoutsHeader
import com.example.proyectoe.ui.dashboard.components.Header
import com.example.proyectoe.ui.dashboard.components.BannerCard


@Composable
fun MainContent(
    modifier:Modifier = Modifier,
    workouts: List <Workout>
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())) {
        Spacer(Modifier.height(16.dp))
        Header()
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