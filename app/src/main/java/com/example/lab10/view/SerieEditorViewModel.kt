package com.example.lab10.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab10.data.SerieApiClient
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class EditorUiState(
    val form: SerieForm = SerieForm(),
    val loading: Boolean = false,
    val saving: Boolean = false,
    val error: String? = null,
    val fieldErrors: Map<String, String> = emptyMap(),
    val saved: Boolean = false,
    val loaded: Boolean = false,
)

class SerieEditorViewModel : ViewModel() {
    private val mutableState = MutableStateFlow(EditorUiState())
    val state = mutableState.asStateFlow()

    fun change(form: SerieForm) {
        val current = mutableState.value
        if (current.saving) return
        val retainedErrors = current.fieldErrors.filterKeys { key ->
            when (key) {
                "name" -> form.name == current.form.name
                "date" -> form.releaseDate == current.form.releaseDate
                "rating" -> form.rating == current.form.rating
                "category" -> form.category == current.form.category
                else -> true
            }
        }
        mutableState.value = current.copy(form = form, fieldErrors = retainedErrors, error = null)
    }

    fun load(id: Int) {
        if (mutableState.value.loaded || mutableState.value.loading) return
        if (id == 0) { mutableState.value = mutableState.value.copy(loaded = true); return }
        mutableState.value = mutableState.value.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                val response = SerieApiClient.service.selectSerie(id)
                if (!response.isSuccessful) throw HttpException(response)
                val serie = response.body() ?: error("El servidor devolvió una respuesta vacía.")
                mutableState.value = EditorUiState(form = SerieForm(serie.name, serie.releaseDate, serie.rating.toString(), serie.category), loaded = true)
            } catch (e: CancellationException) { throw e
            } catch (e: Exception) {
                mutableState.value = mutableState.value.copy(loading = false, error = apiError(e))
            }
        }
    }

    fun save(id: Int) {
        val current = mutableState.value
        if (current.saving || !current.loaded) return
        val errors = current.form.errors()
        if (errors.isNotEmpty()) { mutableState.value = current.copy(fieldErrors = errors); return }
        mutableState.value = current.copy(saving = true, error = null)
        viewModelScope.launch {
            try {
                val serie = current.form.toModel(id.takeIf { it != 0 })
                val response = if (id == 0) SerieApiClient.service.insertSerie(serie) else SerieApiClient.service.updateSerie(id, serie)
                if (!response.isSuccessful) throw HttpException(response)
                mutableState.value = mutableState.value.copy(saving = false, saved = true)
            } catch (e: CancellationException) { throw e
            } catch (e: Exception) {
                mutableState.value = mutableState.value.copy(saving = false, error = apiError(e))
            }
        }
    }
}
