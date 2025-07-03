package com.example.proyectoe.Activity.IntroActivity

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.proyectoe.R

@Composable
fun IntroHeader(){
    Image(
        painter = painterResource(id= R.drawable.intro_pic),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxWidth()
    )
}

