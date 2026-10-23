package com.example.lab10.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun ScreenInicio(state: SeriesUiState, onCollection: () -> Unit, onNew: () -> Unit, onEdit: (Int) -> Unit, onRetry: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        item {
            Column(Modifier.padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Buenas historias.\nSiempre contigo.", style = MaterialTheme.typography.displaySmall)
                Text("Tu espacio para guardar y volver a tus series favoritas.", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyLarge)
            }
        }
        if (state.loading) item { LinearProgressIndicator(Modifier.fillMaxWidth().padding(horizontal = 24.dp)) }
        state.error?.let { message -> item { Box(Modifier.padding(horizontal = 24.dp)) { CatalogMessage(message, true, onRetry) } } }
        state.series.firstOrNull()?.let { featured ->
            item {
                Column(Modifier.padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(
                        onClick = { featured.id?.let(onEdit) }, shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Ver ficha de ${featured.name}" },
                    ) { SerieArtwork(featured, Modifier.fillMaxWidth().height(224.dp), showTitle = true) }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("De tu colección", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                        TextButton(onClick = onCollection) {
                            Text("Explorar todo")
                            Spacer(Modifier.width(6.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
        if (state.series.isEmpty() && !state.loading && state.error == null) item {
            Column(Modifier.padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("La primera historia la eliges tú.", style = MaterialTheme.typography.headlineSmall)
                Text("Añade una serie para empezar tu colección.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Button(onClick = onNew) { Icon(Icons.Default.Add, null); Spacer(Modifier.width(8.dp)); Text("Añadir mi primera serie") }
            }
        }
        if (state.series.size > 1) {
            item {
                Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Más historias", style = MaterialTheme.typography.titleLarge)
                    Text("${state.series.size} en tu colección", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelMedium)
                }
            }
            item {
                LazyRow(contentPadding = PaddingValues(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    items(state.series.drop(1), key = { it.id ?: it.name }) { serie ->
                        Surface(onClick = { serie.id?.let(onEdit) }, color = MaterialTheme.colorScheme.background, modifier = Modifier.width(148.dp)) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                SerieArtwork(serie, Modifier.fillMaxWidth().height(160.dp))
                                Text(serie.name, style = MaterialTheme.typography.titleMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                Text(categories[serie.category] ?: serie.category, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
        item {
            OutlinedButton(onClick = onNew, modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).heightIn(min = 52.dp), shape = RoundedCornerShape(14.dp)) {
                Icon(Icons.Default.Add, null, Modifier.size(20.dp)); Spacer(Modifier.width(8.dp)); Text("Añadir una nueva historia")
            }
        }
    }
}
