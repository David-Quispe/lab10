package com.example.lab10.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.lab10.data.SerieModel

@Composable
fun ContenidoSeriesListado(state: SeriesUiState, onRefresh: () -> Unit, onEdit: (Int) -> Unit, onDelete: (SerieModel) -> Unit, onCreate: () -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    var genre by rememberSaveable { mutableStateOf("") }
    val filtered = remember(state.series, query, genre) {
        filterSeries(state.series, query, genre)
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Column(Modifier.weight(1f)) {
                    Text("Mi colección", style = MaterialTheme.typography.headlineLarge)
                    Text("${state.series.size} historias para volver", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                }
                IconButton(onClick = onRefresh, enabled = !state.loading) { Icon(Icons.Default.Refresh, "Actualizar colección") }
                FilledIconButton(onClick = onCreate, modifier = Modifier.size(48.dp)) { Icon(Icons.Default.Add, "Nueva serie") }
            }
        }
        item {
            OutlinedTextField(
                value = query, onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                label = { Text("Buscar en tu colección") }, singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = { if (query.isNotEmpty()) IconButton(onClick = { query = "" }) { Icon(Icons.Default.Close, "Borrar búsqueda") } },
                shape = RoundedCornerShape(16.dp),
            )
        }
        item {
            LazyRow(contentPadding = PaddingValues(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item { FilterChip(selected = genre.isEmpty(), onClick = { genre = "" }, label = { Text("Todas") }, modifier = Modifier.heightIn(min = 48.dp)) }
                items(categories.entries.toList(), key = { it.key }) { (key, label) ->
                    FilterChip(selected = genre == key, onClick = { genre = key }, label = { Text(label) }, modifier = Modifier.heightIn(min = 48.dp))
                }
            }
        }
        if (state.loading) item { LinearProgressIndicator(Modifier.fillMaxWidth().padding(horizontal = 24.dp)) }
        state.notice?.let { notice -> item { Box(Modifier.padding(horizontal = 24.dp)) { CatalogMessage(notice) } } }
        state.error?.let { message -> item { Box(Modifier.padding(horizontal = 24.dp)) { CatalogMessage(message, true, onRefresh) } } }
        if (!state.loading && state.error == null && filtered.isEmpty()) item {
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(if (state.series.isEmpty()) "Tu colección empieza aquí." else "No encontramos esa historia.", style = MaterialTheme.typography.headlineSmall)
                Text(if (state.series.isEmpty()) "Añade tu primera serie con el botón +." else "Prueba otro nombre o cambia la categoría.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (state.series.isNotEmpty()) TextButton(onClick = { query = ""; genre = "" }) { Text("Limpiar filtros") }
            }
        }
        items(filtered, key = { it.id ?: it.name }) { serie ->
            SerieCollectionItem(serie, onEdit, onDelete)
        }
    }
}

@Composable
private fun SerieCollectionItem(serie: SerieModel, onEdit: (Int) -> Unit, onDelete: (SerieModel) -> Unit) {
    var menu by remember { mutableStateOf(false) }
    Surface(color = MaterialTheme.colorScheme.surfaceContainer, shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
        Row(Modifier.padding(14.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            SerieArtwork(serie, Modifier.width(82.dp).height(126.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                    Text(serie.name, modifier = Modifier.weight(1f).padding(top = 5.dp), style = MaterialTheme.typography.titleLarge, maxLines = 3, overflow = TextOverflow.Ellipsis)
                    Box {
                        IconButton(onClick = { menu = true }, modifier = Modifier.size(48.dp)) { Icon(Icons.Default.MoreVert, "Más opciones de ${serie.name}") }
                        DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                            DropdownMenuItem(text = { Text("Eliminar serie", color = MaterialTheme.colorScheme.error) }, leadingIcon = { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) }, onClick = { menu = false; onDelete(serie) })
                        }
                    }
                }
                Text(categories[serie.category] ?: serie.category, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                Text(displayDate(serie.releaseDate), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelMedium)
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    RatingLabel(serie.rating)
                    TextButton(onClick = { serie.id?.let(onEdit) }) { Text("Editar"); Spacer(Modifier.width(4.dp)); Icon(Icons.Default.Edit, null, Modifier.size(16.dp)) }
                }
            }
        }
    }
}
