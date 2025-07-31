package com.example.proyectoe.ui.dashboard.components
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.example.proyectoe.R

// Clase de datos para representar cada banner con su video
data class BannerItem(
    val imageRes: Int,
    val youtubeVideoId: String
)

@Composable
fun BannerClickleable() {
    // Lista de banners con sus videos asociados
    val banners = listOf(
        BannerItem(
            imageRes = R.drawable.unamas,
            youtubeVideoId = "7zsGpJVkZi4" // ID del video de YouTube
        ),
        BannerItem(
            imageRes = R.drawable.pereza,
            youtubeVideoId = "H_1bxsZ_Idg" // ID del video de YouTube
        ),
        BannerItem(
            imageRes = R.drawable.dieta,
            youtubeVideoId = "El7sSxEqVSY"
        ),
        BannerItem(
            imageRes = R.drawable.meta,
            youtubeVideoId = "PSZQpV3a9jk"
        ),
        BannerItem(
            imageRes = R.drawable.proyecto,
            youtubeVideoId = "Us1sQ6MfdIM"
        )
    )

    var currentIndex by remember { mutableStateOf(0) }
    val context = LocalContext.current

    // Cambio automático cada 10 segundos
    LaunchedEffect(Unit) {
        while (true) {
            delay(10000)
            currentIndex = (currentIndex + 1) % banners.size
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 7.dp)
            .clip(RoundedCornerShape(10.dp))
    ) {
        Crossfade(
            targetState = currentIndex,
            animationSpec = tween(1000)
        ) { index ->
            val banner = banners[index]
            Image(
                painter = painterResource(banner.imageRes),
                contentDescription = "Promoción ${index + 1}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        // Al hacer clic, se abre el video de YouTube
                        linkYoutube(context, banner.youtubeVideoId)
                    }
            )
        }
    }
}

// Función para abrir un video de YouTube
fun linkYoutube(context: Context, videoId: String) {
    val url = if (videoId.length == 11) {
        "https://www.youtube.com/watch?v=$videoId"
    } else {
        "https://www.youtube.com/shorts/$videoId"
    }

    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(url)
        setPackage("com.google.android.youtube")
    }

    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }
}