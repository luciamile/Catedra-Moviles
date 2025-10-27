package com.example.countryweatherapp.model

import com.example.countryweatherapp.utils.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    // Endpoint para obtener el clima actual [cite: 24]
    @GET("current.json")
    suspend fun getCurrentWeather(
        @Query("key") apiKey: String = Constants.WEATHER_API_KEY, // Se usa la clave del archivo Constants [cite: 13, 25]
        @Query("q") capital: String // La capital para la cual se busca el clima [cite: 24]
    ): Response<WeatherResponse>
}