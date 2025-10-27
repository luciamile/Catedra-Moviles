package com.example.countryweatherapp.model

import com.google.gson.annotations.SerializedName

// Estructura para la respuesta del clima actual
data class WeatherResponse(
    @SerializedName("current") val current: CurrentWeather
)

data class CurrentWeather(
    @SerializedName("temp_c") val tempC: Double, // Temperatura en °C
    @SerializedName("temp_f") val tempF: Double, // Temperatura en °F
    @SerializedName("condition") val condition: WeatherCondition, // Condición del clima
    @SerializedName("wind_kph") val windKph: Double, // Velocidad del viento (kph)
    @SerializedName("wind_mph") val windMph: Double, // Velocidad del viento (mph)
    @SerializedName("humidity") val humidity: Int // Humedad
)

data class WeatherCondition(
    @SerializedName("text") val text: String, // Texto de la condición
    @SerializedName("icon") val icon: String // Icono de estado (URL)
)