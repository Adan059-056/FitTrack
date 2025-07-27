package com.example.proyectoe.ui.intro.componets

import androidx.compose.foundation.clickable
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
//import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight

@Composable
fun IntroFooter(onSignInClick: () -> Unit){
    val annotatedString = buildAnnotatedString {
        append("¿No tienes cuenta? ") // He cambiado el texto para que tenga más sentido en una pantalla de Sing In
        pushStringAnnotation(tag = "SIGN_IN", annotation = "SIGN_IN_CLICK")
        withStyle(SpanStyle(color = colorResource(R.color.orange), fontWeight = FontWeight.Bold)) {
            append("Regístrate") // Y aquí el texto para que invite al registro
        }
        pop()
    }

    Text(
        text = annotatedString,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { // El clickable se aplica a todo el Text
                // Llamamos directamente a onSignInClick cuando se hace clic en cualquier parte del Text,
                // asumiendo que es el único punto de interacción en este footer.
                // Si solo quieres que "Regístrate" sea clicable, puedes usar ClickableText o procesar las anotaciones.
                // Para simplificar, asumimos que cualquier clic en este texto dispara la navegación a registro.
                onSignInClick()
            },
        textAlign = TextAlign.Center,
        color = Color.White,
        fontSize = 18.sp
    )
}