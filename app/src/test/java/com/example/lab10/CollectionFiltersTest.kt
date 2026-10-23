package com.example.lab10

import com.example.lab10.data.SerieModel
import com.example.lab10.view.filterSeries
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CollectionFiltersTest {
    private val series = listOf(
        SerieModel(1, "Misión Aurora", "2022-09-05", 5, "action"),
        SerieModel(2, "Risas en Casa", "2021-06-18", 4, "comedy"),
        SerieModel(3, "Vidas Cruzadas", "2019-11-20", 4, "drama"),
    )

    @Test fun searchIgnoresAccentsCaseAndOuterWhitespace() {
        assertEquals(listOf(1), filterSeries(series, "  MISION  ", "").map { it.id })
    }

    @Test fun categoryAndSearchAreCombined() {
        assertTrue(filterSeries(series, "aurora", "comedy").isEmpty())
        assertEquals(listOf(1), filterSeries(series, "aurora", "action").map { it.id })
    }

    @Test fun clearingFiltersRestoresOriginalCollection() {
        assertEquals(series, filterSeries(series, "", ""))
    }

    @Test fun unknownQueryOrEmptyCollectionProducesNoResults() {
        assertTrue(filterSeries(series, "inexistente", "").isEmpty())
        assertTrue(filterSeries(emptyList(), "", "").isEmpty())
    }
}
