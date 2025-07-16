package com.example.proyectoe.ui.dashboard.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.proyectoe.R
import android.net.Uri
import coil.compose.rememberAsyncImagePainter // <-- Para rememberAsyncImagePainter
import androidx.compose.ui.draw.clip // <-- Para el modificador .clip()
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.unit.sp

@Composable
fun Header(userName: String,
           profilePhotoUri: Uri?){
    ConstraintLayout (modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 16.dp)
    ){
        val (welcomeRef,nameRef,profileRef,resumenRef) = createRefs()
        Text(
            text = "Bienvenido de nuevo",
            color = Color.White,
            modifier = Modifier.constrainAs(welcomeRef){
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            }
        )
        Text(text=userName,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.constrainAs(nameRef){
                top.linkTo(welcomeRef.bottom)
                start.linkTo(parent.start)
                bottom.linkTo(profileRef.bottom)
            })
        val painter = if (profilePhotoUri != null) {
            rememberAsyncImagePainter(model = profilePhotoUri)
        } else {
            painterResource(R.drawable.btn_4) // Imagen por defecto
        }
        Image(
            painter = painter, // <-- ¡Usa el painter con la foto del usuario!
            contentDescription = "Foto de perfil del usuario",
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape) // Para hacerla circular
                .constrainAs(profileRef){
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                },
            contentScale = ContentScale.Crop
        )

    }
}