package com.example.lab10.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ContenidoSerieEliminar(state: SeriesUiState, onConfirm: () -> Unit, onCancel: () -> Unit) {
    val serie = state.pendingDelete ?: return
    AlertDialog(
        onDismissRequest = { if (!state.deleting) onCancel() },
        icon = { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(28.dp)) },
        title = { Text("¿Eliminar esta historia?") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("«${serie.name}» dejará de formar parte de tu colección.")
                Text("Esta acción no se puede deshacer.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        confirmButton = {
            Button(onClick = onConfirm, enabled = !state.deleting, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error, contentColor = MaterialTheme.colorScheme.onError)) {
                Text(if (state.deleting) "Eliminando…" else "Eliminar serie")
            }
        },
        dismissButton = { TextButton(onClick = onCancel, enabled = !state.deleting) { Text("Conservar") } },
    )
}
