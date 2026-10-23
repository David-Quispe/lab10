package com.example.lab10.view

import com.example.lab10.data.SerieModel
import java.text.Normalizer
import java.util.Locale

private fun String.searchKey(): String = Normalizer.normalize(trim(), Normalizer.Form.NFD)
    .replace(Regex("\\p{M}+"), "")
    .lowercase(Locale.ROOT)

fun filterSeries(series: List<SerieModel>, query: String, category: String): List<SerieModel> {
    val term = query.searchKey()
    return series.filter {
        (category.isEmpty() || it.category == category) && it.name.searchKey().contains(term)
    }
}
