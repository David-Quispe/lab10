package com.example.lab10.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContenidoSerieEditar(id: Int, onSaved: () -> Unit, onCancel: () -> Unit, editor: SerieEditorViewModel = viewModel()) {
    val state by editor.state.collectAsStateWithLifecycle()
    val form = state.form
    val focus = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    val nameFocus = remember { FocusRequester() }
    val dateFocus = remember { FocusRequester() }
    val ratingFocus = remember { FocusRequester() }
    var datePicker by rememberSaveable { mutableStateOf(false) }
    val enabled = state.loaded && !state.saving
    LaunchedEffect(id) { editor.load(id) }
    LaunchedEffect(state.saved) { if (state.saved) onSaved() }
    LaunchedEffect(state.validationAttempt) {
        when {
            "name" in state.fieldErrors -> nameFocus.requestFocus()
            "date" in state.fieldErrors -> dateFocus.requestFocus()
            "rating" in state.fieldErrors -> ratingFocus.requestFocus()
        }
    }
    val save = {
        focus.clearFocus()
        keyboard?.hide()
        editor.save(id)
    }
    Column(Modifier.fillMaxSize().imePadding()) {
        Column(
            Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(if (id == 0) "Nueva serie" else "Editar historia", style = MaterialTheme.typography.headlineLarge)
                Text(if (id == 0) "Dale un lugar en tu colección." else "Los detalles hacen la diferencia.", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyLarge)
            }
            if (id != 0) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("ID de solo lectura", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("#$id", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                }
            }
            if (state.loading) LinearProgressIndicator(Modifier.fillMaxWidth())
            state.error?.let { CatalogMessage(it, true, if (!state.loaded) ({ editor.load(id) }) else null) }
            if (state.fieldErrors.isNotEmpty()) Text(
                "Revisa los campos señalados para continuar.", color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
            )
            OutlinedTextField(
                value = form.name, onValueChange = { editor.change(form.copy(name = it)) },
                label = { Text("Nombre de la serie") }, placeholder = { Text("¿Qué historia vas a guardar?") },
                modifier = Modifier.fillMaxWidth().focusRequester(nameFocus), singleLine = true,
                shape = RoundedCornerShape(14.dp), enabled = enabled,
                isError = "name" in state.fieldErrors,
                supportingText = { Text(state.fieldErrors["name"] ?: "Máximo 100 caracteres") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) }),
            )
            OutlinedTextField(
                value = form.releaseDate, onValueChange = { editor.change(form.copy(releaseDate = it)) },
                label = { Text("Fecha de estreno") }, placeholder = { Text("AAAA-MM-DD") },
                modifier = Modifier.fillMaxWidth().focusRequester(dateFocus), singleLine = true,
                shape = RoundedCornerShape(14.dp), enabled = enabled,
                trailingIcon = { IconButton(onClick = { focus.clearFocus(); keyboard?.hide(); datePicker = true }, enabled = enabled) { Icon(Icons.Default.DateRange, "Elegir fecha de estreno") } },
                isError = "date" in state.fieldErrors,
                supportingText = { Text(state.fieldErrors["date"] ?: "Escríbela o utiliza el calendario") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) }),
            )
            OutlinedTextField(
                value = form.rating, onValueChange = { editor.change(form.copy(rating = it)) },
                label = { Text("Tu valoración") }, placeholder = { Text("Por ejemplo, 5") },
                modifier = Modifier.fillMaxWidth().focusRequester(ratingFocus), singleLine = true,
                shape = RoundedCornerShape(14.dp), enabled = enabled,
                isError = "rating" in state.fieldErrors,
                supportingText = { Text(state.fieldErrors["rating"] ?: "Utiliza un número entero") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focus.clearFocus(); keyboard?.hide() }),
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Género", style = MaterialTheme.typography.titleMedium)
                categories.entries.chunked(2).forEach { pair ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        pair.forEach { (key, label) ->
                            FilterChip(
                                selected = form.category == key,
                                onClick = { focus.clearFocus(); keyboard?.hide(); editor.change(form.copy(category = key)) },
                                label = { Text(label) }, enabled = enabled,
                                modifier = Modifier.weight(1f).heightIn(min = 52.dp),
                                leadingIcon = { if (form.category == key) Icon(Icons.Default.Check, null, Modifier.size(18.dp)) },
                                shape = RoundedCornerShape(12.dp),
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onCancel, enabled = !state.saving, modifier = Modifier.weight(1f).heightIn(min = 52.dp), shape = RoundedCornerShape(14.dp), contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp)) { Text("Cancelar") }
            Button(onClick = save, enabled = enabled, modifier = Modifier.weight(1.2f).heightIn(min = 52.dp), shape = RoundedCornerShape(14.dp), contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp)) {
                if (state.saving) { CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp); Spacer(Modifier.width(8.dp)) }
                Text(if (state.saving) "Guardando…" else "Guardar serie")
            }
        }
    }
    if (datePicker) {
        val format = remember { SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).apply { timeZone = TimeZone.getTimeZone("UTC"); isLenient = false } }
        val initial = remember(form.releaseDate) { runCatching { format.parse(form.releaseDate)?.time }.getOrNull() }
        val calendar = rememberDatePickerState(initialSelectedDateMillis = initial)
        DatePickerDialog(
            onDismissRequest = { datePicker = false },
            confirmButton = { TextButton(onClick = {
                calendar.selectedDateMillis?.let { editor.change(form.copy(releaseDate = format.format(Date(it)))) }
                datePicker = false
            }, enabled = calendar.selectedDateMillis != null) { Text("Elegir fecha") } },
            dismissButton = { TextButton(onClick = { datePicker = false }) { Text("Cancelar") } },
        ) { DatePicker(state = calendar, title = { Text("Fecha de estreno", modifier = Modifier.padding(24.dp)) }) }
    }
}
