package com.example.lab10

import com.example.lab10.view.SerieForm
import com.google.gson.Gson
import org.junit.Assert.*
import org.junit.Test

class SerieFormTest {
    private val valid = SerieForm("Serie de prueba", "2024-02-29", "5", "drama")

    @Test fun acceptsLeapDayAndProducesApiFieldNames() {
        assertTrue(valid.errors().isEmpty())
        val json = Gson().toJsonTree(valid.toModel(null)).asJsonObject
        assertEquals("2024-02-29", json["release_date"].asString)
        assertFalse(json.has("releaseDate"))
        assertFalse(json.has("id"))
    }

    @Test fun rejectsImpossibleDate() {
        assertTrue("date" in valid.copy(releaseDate = "2023-02-29").errors())
        assertTrue("date" in valid.copy(releaseDate = "2026-04-31").errors())
    }

    @Test fun requiresExactDateFormat() {
        listOf("", "23/10/2026", "2026-1-01", "2026-10-23extra").forEach {
            assertTrue("date" in valid.copy(releaseDate = it).errors())
        }
    }

    @Test fun rejectsMissingOrOversizedName() {
        assertTrue("name" in valid.copy(name = "   ").errors())
        assertTrue("name" in valid.copy(name = "a".repeat(101)).errors())
    }

    @Test fun rejectsInvalidOrOverflowingRating() {
        listOf("", "texto", "4.5", "9999999999999999").forEach {
            assertTrue("rating" in valid.copy(rating = it).errors())
        }
    }

    @Test fun preservesIdForUpdatesAndTrimsName() {
        val model = valid.copy(name = "  Serie  ").toModel(3)
        assertEquals(3, model.id)
        assertEquals("Serie", model.name)
        assertTrue("category" in valid.copy(category = "otro").errors())
    }
}
