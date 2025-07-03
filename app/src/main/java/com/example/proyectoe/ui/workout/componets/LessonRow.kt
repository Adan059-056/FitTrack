package com.example.proyectoe.ui.workout.componets

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectoe.data.model.Lession
import com.example.proyectoe.R

@Composable
fun LessonRow(lession: Lession){
    val context = LocalContext.current
    Row(modifier = Modifier
        .fillMaxWidth()
        .background(Color(0xFF1c213c), RoundedCornerShape(8.dp))
        .clickable {
        val appIntent = Intent(Intent.ACTION_VIEW,
            Uri.parse("vnd.youtube:${lession.link}")
        )
            val webIntent=Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.youtube.com/watch?v=${lession.link}")
            )
            try{
                context.startActivity(appIntent)
            }catch(_: ActivityNotFoundException){
                context.startActivity(webIntent)
            }
        }
        .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        val ctx = LocalContext.current
        val thumbId = ctx.resources.getIdentifier(
            lession.picPath,"drawable", ctx.packageName
        )
        Image(painter = painterResource(thumbId),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp))
            )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(lession.title, color = Color.White, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(lession.duration, color = Color.White, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.weight(1f))
        Image(painter = painterResource(R.drawable.play),
            contentDescription = null
        )
    }
}