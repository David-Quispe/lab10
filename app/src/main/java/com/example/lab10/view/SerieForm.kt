package com.example.lab10.view

import com.example.lab10.data.SerieModel
import java.text.SimpleDateFormat
import java.util.Locale

data class SerieForm(
    val name: String = "",
    val releaseDate: String = "",
    val rating: String = "",
    val category: String = "drama",
) {
    fun errors(): Map<String, String> = buildMap {
        if (name.isBlank() || name.trim().length > 100) put("name", "Escribe un nombre de 1 a 100 caracteres.")
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).apply { isLenient = false }
        val validDate = Regex("[0-9]{4}-[0-9]{2}-[0-9]{2}").matches(releaseDate) &&
            runCatching { dateFormat.parse(releaseDate) != null }.getOrDefault(false)
        if (!validDate) put("date", "Usa una fecha válida con formato AAAA-MM-DD.")
        if (rating.toIntOrNull() == null) put("rating", "Escribe una valoración numérica entera.")
        if (category !in categories) put("category", "Selecciona una categoría válida.")
    }

    fun toModel(id: Int?) = SerieModel(id, name.trim(), releaseDate, rating.toInt(), category)
}
