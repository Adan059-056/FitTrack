package com.example.proyectoe.Activity.IntroActivity

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import com.example.proyectoe.R
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color

@Composable
fun IntroFooter(){
    Text(
        text = buildAnnotatedString {
            append("Ingresa con tu cuenta ")
            withStyle(SpanStyle(color = colorResource(R.color.orange))) {
                append("Sing in")
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        textAlign = TextAlign.Center,
        color = Color.White,
        fontSize = 18.sp
    )
}