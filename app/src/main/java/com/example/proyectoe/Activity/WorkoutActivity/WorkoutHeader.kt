package com.example.proyectoe.Activity.WorkoutActivity

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.proyectoe.Model.Workout
import com.example.proyectoe.R
import androidx.compose.foundation.layout.Box

@Composable
fun WorkoutHeader(workout: Workout, onBack:()->Unit={})
{
    val context = LocalContext.current
    val picId = context.resources.getIdentifier(
        workout.picPath, "drawable", context.packageName
    )
    ConstraintLayout (modifier = Modifier
        .fillMaxWidth()
        .height(375.dp)){
        val(imageRef,backRef, favRef, overlayRef) = createRefs()
        Image(painter = painterResource(id=picId),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .constrainAs (imageRef){
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
        )
        IconButton(
            onClick = onBack,
            modifier = Modifier.constrainAs (backRef){
                top.linkTo(parent.top, margin = 48.dp)
                start.linkTo(parent.start, margin = 16.dp)
            }
        ) {
            Image(
                painter = painterResource(R.drawable.back),
                contentDescription = null
            )
        }


        IconButton(
            onClick = {},
            modifier = Modifier.constrainAs (favRef){
                top.linkTo(parent.top, margin = 48.dp)
                end.linkTo(parent.end, margin = 16.dp)
            }
        ) {
            Image(
                painter = painterResource(R.drawable.fav_icon),
                contentDescription = null
            )
        }
        Box(modifier = Modifier
            .constrainAs (overlayRef){
                bottom.linkTo(imageRef.bottom)
                start.linkTo(imageRef.start)
                end.linkTo(imageRef.end)
                height= Dimension.value(130.dp)
                width= Dimension.fillToConstraints
            }
            .background(brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color(0xff0b0b0c),
                    Color(0xff101322)
                )
            ))
        )
    }
}