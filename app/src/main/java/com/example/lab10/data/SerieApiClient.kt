package com.example.lab10.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SerieApiClient {
    // El emulador accede al servidor local de la computadora mediante 10.0.2.2.
    const val BASE_URL = "http://10.0.2.2:8000/"
    val service: SerieApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SerieApiService::class.java)
    }
}
