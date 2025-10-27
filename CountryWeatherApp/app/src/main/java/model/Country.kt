package com.example.countryweatherapp.model

import com.google.gson.annotations.SerializedName

// Estructuras de datos para los campos esperados
data class Country(
    @SerializedName("name") val name: Name,
    @SerializedName("capital") val capital: List<String>?, // Puede ser nulo o lista
    @SerializedName("region") val region: String, //
    @SerializedName("subregion") val subregion: String?, //
    @SerializedName("population") val population: Long, //
    @SerializedName("flags") val flags: Flags, //
    // Opcional: códigos ISO, monedas, idiomas, latlng, si los necesitas en detalle
)

data class Name(
    @SerializedName("common") val common: String,
    @SerializedName("official") val official: String
)

data class Flags(
    @SerializedName("png") val png: String, // URL del icono
    @SerializedName("alt") val alt: String?
)