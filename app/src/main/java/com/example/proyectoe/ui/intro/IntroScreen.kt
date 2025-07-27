// src/main/java/com/example/proyectoe/ui/intro/IntroScreen.kt
package com.example.proyectoe.ui.intro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyectoe.R
import com.example.proyectoe.ui.intro.componets.IntroHeader
import com.example.proyectoe.ui.intro.componets.IntroFooter
import com.example.proyectoe.ui.intro.componets.IntroActionButton
import com.example.proyectoe.ui.intro.componets.IntroDescription

@Composable
fun IntroScreen(
    onStartClick: () -> Unit,
    onFooterSignInClick: () -> Unit // <-- Nuevo callback para el footer
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.mainColor))
    ) {
        item { IntroHeader() }
        item { IntroDescription() }
        item { IntroActionButton(onStartClick) }
        item { IntroFooter(onFooterSignInClick) } // <-- Pasa el callback al footer
    }
}

@Preview
@Composable
fun IntroScreenPreview() {
    IntroScreen(onStartClick = {}, onFooterSignInClick = {})
}