package com.example.lab10.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ContenidoSerieEditar(id: Int, onSaved: () -> Unit, onCancel: () -> Unit, editor: SerieEditorViewModel = viewModel()) {
    val state by editor.state.collectAsStateWithLifecycle()
    LaunchedEffect(id) { editor.load(id) }
    LaunchedEffect(state.saved) { if (state.saved) onSaved() }
    val form = state.form
    Column(
        Modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(if (id == 0) "Nueva serie" else "Editar serie", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(if (id == 0) "Completa los datos para añadirla a tu colección." else "Actualiza los datos del registro #$id.")
        if (state.loading) LinearProgressIndicator(Modifier.fillMaxWidth())
        state.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
            if (!state.loaded) OutlinedButton(onClick = { editor.load(id) }) { Text("Reintentar carga") }
        }
        val enabled = state.loaded && !state.saving
        if (id != 0) OutlinedTextField(value = id.toString(), onValueChange = {}, label = { Text("ID (solo lectura)") }, readOnly = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = form.name, onValueChange = { editor.change(form.copy(name = it)) }, label = { Text("Nombre") }, singleLine = true, enabled = enabled, modifier = Modifier.fillMaxWidth(), isError = "name" in state.fieldErrors,
            supportingText = { state.fieldErrors["name"]?.let { Text(it) } })
        OutlinedTextField(value = form.releaseDate, onValueChange = { editor.change(form.copy(releaseDate = it)) }, label = { Text("Fecha de estreno") }, placeholder = { Text("AAAA-MM-DD") }, singleLine = true, enabled = enabled, modifier = Modifier.fillMaxWidth(), isError = "date" in state.fieldErrors,
            supportingText = { Text(state.fieldErrors["date"] ?: "Formato: AAAA-MM-DD") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii))
        OutlinedTextField(value = form.rating, onValueChange = { editor.change(form.copy(rating = it)) }, label = { Text("Valoración") }, singleLine = true, enabled = enabled, modifier = Modifier.fillMaxWidth(), isError = "rating" in state.fieldErrors,
            supportingText = { state.fieldErrors["rating"]?.let { Text(it) } }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        Text("Categoría", style = MaterialTheme.typography.labelLarge)
        Column {
            categories.entries.chunked(2).forEach { pair ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    pair.forEach { (key, label) ->
                        FilterChip(selected = form.category == key, onClick = { editor.change(form.copy(category = key)) }, label = { Text(label) }, enabled = enabled)
                    }
                }
            }
        }
        Button(onClick = { editor.save(id) }, enabled = enabled, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
            Text(if (state.saving) "Guardando…" else "Guardar serie")
        }
        TextButton(onClick = onCancel, enabled = !state.saving, modifier = Modifier.fillMaxWidth()) { Text("Cancelar") }
    }
}
