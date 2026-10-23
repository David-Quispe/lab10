package com.example.lab10.view

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun ContenidoSerieEliminar(state: SeriesUiState, onConfirm: () -> Unit, onCancel: () -> Unit) {
    val serie = state.pendingDelete ?: return
    AlertDialog(
        onDismissRequest = { if (!state.deleting) onCancel() },
        title = { Text("¿Eliminar serie?") },
        text = { Text("Se eliminará «${serie.name}» de la colección." + (state.error?.let { "\n\n$it" } ?: "")) },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = !state.deleting) {
                Text(if (state.deleting) "Eliminando…" else "Eliminar", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = { TextButton(onClick = onCancel, enabled = !state.deleting) { Text("Cancelar") } },
    )
}
