package com.example.lab10.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lab10.data.SerieModel

val categories = linkedMapOf("drama" to "Drama", "comedy" to "Comedia", "action" to "Acción", "horror" to "Terror")

@Composable
fun ContenidoSeriesListado(state: SeriesUiState, onRefresh: () -> Unit, onEdit: (Int) -> Unit, onDelete: (SerieModel) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Tu colección", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("${state.series.size} series registradas", style = MaterialTheme.typography.bodyMedium)
                }
                TextButton(onClick = onRefresh, enabled = !state.loading) { Text("Actualizar") }
            }
        }
        if (state.loading) item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
        state.notice?.let { notice -> item { Text(notice, color = MaterialTheme.colorScheme.primary) } }
        state.error?.let { message -> item {
            Text(message, color = MaterialTheme.colorScheme.error)
            OutlinedButton(onClick = onRefresh, enabled = !state.loading) { Text("Reintentar") }
        } }
        if (!state.loading && state.error == null && state.series.isEmpty()) item {
            Text("Aún no hay series. Pulsa Nueva serie para registrar la primera.")
        }
        items(state.series, key = { it.id ?: it.name }) { serie ->
            OutlinedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(serie.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    Text("ID ${serie.id} · ${categories[serie.category] ?: serie.category}", style = MaterialTheme.typography.labelLarge)
                    Text("Estreno: ${serie.releaseDate}  ·  Valoración: ${serie.rating}", style = MaterialTheme.typography.bodyMedium)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { serie.id?.let(onEdit) }) { Text("Editar") }
                        TextButton(onClick = { onDelete(serie) }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
                    }
                }
            }
        }
    }
}
