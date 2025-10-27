package com.example.countryweatherapp.model

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface RestCountriesService {
    // Endpoint para obtener todos los países (usado para listar regiones iniciales) [cite: 18]
    @GET("v3.1/all")
    suspend fun getAllCountries(): Response<List<Country>>

    // Endpoint para obtener países por región [cite: 19]
    @GET("v3.1/region/{region}")
    suspend fun getCountriesByRegion(@Path("region") region: String): Response<List<Country>>
}