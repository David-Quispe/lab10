package com.example.lab10.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lab10.data.SerieModel
import com.example.lab10.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Locale

val categories = linkedMapOf("drama" to "Drama", "comedy" to "Comedia", "action" to "Acción", "horror" to "Terror")

fun displayDate(raw: String): String = runCatching {
    val parsed = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).apply { isLenient = false }.parse(raw)
    SimpleDateFormat("d MMM yyyy", Locale.forLanguageTag("es")).format(parsed!!)
}.getOrDefault(raw)

@Composable
fun SerieArtwork(serie: SerieModel, modifier: Modifier = Modifier, showTitle: Boolean = false) {
    val (background, accent) = when (serie.category) {
        "comedy" -> ComedyCover to ComedyMotif
        "action" -> ActionCover to ActionMotif
        "horror" -> HorrorCover to HorrorMotif
        else -> DramaCover to DramaMotif
    }
    BoxWithConstraints(modifier.clip(RoundedCornerShape(16.dp)).background(background).clearAndSetSemantics {}) {
        Canvas(Modifier.matchParentSize()) {
            when (serie.category) {
                "action" -> repeat(7) { i ->
                    val x = size.width * (i - 2) / 4
                    drawLine(accent.copy(alpha = 0.22f), Offset(x, size.height), Offset(x + size.width, 0f), 2.dp.toPx())
                }
                "comedy" -> {
                    drawCircle(accent.copy(alpha = 0.17f), size.width * 0.55f, Offset(size.width * 0.8f, size.height * 0.25f))
                    drawCircle(accent.copy(alpha = 0.3f), size.width * 0.3f, Offset(size.width * 0.1f, size.height * 0.85f), style = Stroke(2.dp.toPx()))
                }
                else -> repeat(4) { i ->
                    drawCircle(accent.copy(alpha = 0.14f), size.width * (0.3f + i * 0.18f), Offset(size.width * 0.8f, size.height * 0.38f), style = Stroke(1.dp.toPx()))
                }
            }
        }
        Text(
            serie.name.trim().take(1).uppercase(),
            modifier = Modifier.align(Alignment.Center).offset(y = (-8).dp),
            fontFamily = FontFamily.Serif, fontWeight = FontWeight.Normal,
            fontSize = (maxWidth.value * 0.67f).sp, lineHeight = (maxWidth.value * 0.74f).sp,
            color = accent.copy(alpha = 0.7f),
        )
        if (showTitle) {
            Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.SpaceBetween) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text((categories[serie.category] ?: serie.category).uppercase(), color = Color.White, fontSize = 11.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Medium)
                    Text(serie.id?.toString()?.padStart(2, '0') ?: "", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                }
                Column {
                    Text(serie.name, color = Color.White, fontSize = 28.sp, lineHeight = 32.sp, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(displayDate(serie.releaseDate), color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun RatingLabel(rating: Int, modifier: Modifier = Modifier) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(Icons.Default.Star, null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
        Text("$rating", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun CatalogMessage(text: String, error: Boolean = false, onRetry: (() -> Unit)? = null) {
    Surface(color = if (error) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceContainerHigh, shape = RoundedCornerShape(14.dp)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                if (!error) Icon(Icons.Default.CheckCircle, null, Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                Text(text, style = MaterialTheme.typography.bodyMedium, color = if (error) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurface)
            }
            if (onRetry != null) TextButton(onClick = onRetry) { Text("Volver a intentar") }
        }
    }
}
