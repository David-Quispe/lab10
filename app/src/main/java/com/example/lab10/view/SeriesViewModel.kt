package com.example.lab10.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab10.data.SerieApiClient
import com.example.lab10.data.SerieModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

data class SeriesUiState(
    val series: List<SerieModel> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
    val notice: String? = null,
    val pendingDelete: SerieModel? = null,
    val deleting: Boolean = false,
)

fun apiError(error: Exception): String = when (error) {
    is IOException -> "No se pudo conectar con Django. Comprueba que el servidor esté encendido y vuelve a intentar."
    is HttpException -> "El servidor respondió con error ${error.code()}. Vuelve a intentar."
    else -> error.message ?: "No se pudo completar la operación. Vuelve a intentar."
}

class SeriesViewModel : ViewModel() {
    private val mutableState = MutableStateFlow(SeriesUiState())
    val state = mutableState.asStateFlow()

    fun requestDelete(serie: SerieModel) {
        mutableState.value = mutableState.value.copy(pendingDelete = serie, error = null, notice = null)
    }

    fun cancelDelete() {
        if (!mutableState.value.deleting) mutableState.value = mutableState.value.copy(pendingDelete = null)
    }

    fun delete() {
        val serie = mutableState.value.pendingDelete ?: return
        val id = serie.id ?: return
        if (mutableState.value.deleting) return
        mutableState.value = mutableState.value.copy(deleting = true, error = null)
        viewModelScope.launch {
            try {
                val response = SerieApiClient.service.deleteSerie(id)
                if (!response.isSuccessful) throw HttpException(response)
                mutableState.value = mutableState.value.copy(
                    series = mutableState.value.series.filterNot { it.id == id },
                    pendingDelete = null, deleting = false, notice = "Serie eliminada correctamente.",
                )
            } catch (e: CancellationException) { throw e
            } catch (e: Exception) {
                mutableState.value = mutableState.value.copy(deleting = false, error = apiError(e))
            }
        }
    }

    fun refresh(notice: String? = null) {
        if (mutableState.value.loading) return
        mutableState.value = mutableState.value.copy(loading = true, error = null, notice = notice)
        viewModelScope.launch {
            try {
                val series = SerieApiClient.service.selectSeries()
                mutableState.value = SeriesUiState(series = series, notice = notice)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                mutableState.value = mutableState.value.copy(loading = false, error = apiError(e))
            }
        }
    }
}
