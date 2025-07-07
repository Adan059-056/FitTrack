package com.example.proyectoe.ui.intro



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyectoe.R
import com.example.proyectoe.ui.components.IntroHeader
import com.example.proyectoe.ui.components.IntroFooter
import com.example.proyectoe.ui.components.IntroActionButton
import com.example.proyectoe.ui.intro.componets.IntroDescription

@Composable
@Preview
fun IntroScreen(onStartClick:()-> Unit={}){
    LazyColumn(
        modifier = Modifier
        .fillMaxSize()
        .background(colorResource(R.color.mainColor))
    )
    {
        item{ IntroHeader() }
        item{ IntroDescription() }
        item{ IntroActionButton(onStartClick)}
        item{ IntroFooter()}
    }
}