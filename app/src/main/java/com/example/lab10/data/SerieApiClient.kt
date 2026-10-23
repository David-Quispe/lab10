package com.example.lab10.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SerieApiClient {
    // Django escucha en la IP de la computadora. También sirve al emulador.
    const val BASE_URL = "http://172.22.159.90:8000/"
    val service: SerieApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SerieApiService::class.java)
    }
}
