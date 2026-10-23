package com.example.lab10.data

import com.google.gson.annotations.SerializedName

data class SerieModel(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("name") val name: String,
    @SerializedName("release_date") val releaseDate: String,
    @SerializedName("rating") val rating: Int,
    @SerializedName("category") val category: String,
)
