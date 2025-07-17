package com.example.proyectoe.ui.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
//import com.example.proyectoe.ui.auth.SecondaryColor
import androidx.compose.ui.Alignment

@Composable
fun UserResumeCard(
title: String,
cardColor: Color,
modifier: Modifier = Modifier,
content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth(0.9f)
            .clip(MaterialTheme.shapes.medium)
            .background(cardColor)
            .padding(vertical = 8.dp)


    ) {
        Text(
            text = title.uppercase(),
            color = Color.White,
            fontSize = 13.sp,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 4.dp)
        )
        content()

    }
}