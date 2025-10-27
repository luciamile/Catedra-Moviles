package com.example.countryweatherapp.controller

import com.example.countryweatherapp.model.Country
import com.example.countryweatherapp.model.RestCountriesService
import com.example.countryweatherapp.model.WeatherResponse
import com.example.countryweatherapp.model.WeatherService
import com.example.countryweatherapp.utils.RetrofitClient

class CountryRepository {

    private val restCountriesService = RetrofitClient.restCountriesInstance.create(RestCountriesService::class.java)
    private val weatherService = RetrofitClient.weatherApiInstance.create(WeatherService::class.java)

    // Lógica para obtener todas las regiones ÚNICAS (Usado en MainActivity)
    suspend fun getAllRegions(): Result<List<String>> {
        return try {
            val response = restCountriesService.getAllCountries()
            if (response.isSuccessful && response.body() != null) {
                val regions = response.body()!!
                    .map { it.region }
                    .filter { it.isNotBlank() }
                    .distinct()
                    .sorted()
                Result.success(regions)
            } else {
                Result.failure(Exception("Error al cargar los países: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // NUEVA FUNCIÓN: Obtiene la lista completa de países (Necesaria para CountryDetailActivity)
    suspend fun getAllCountriesList(): Result<List<Country>> {
        return try {
            val response = restCountriesService.getAllCountries()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al cargar la lista global de países: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Función para obtener países por región (Usado en CountryListActivity)
    suspend fun getCountriesByRegion(region: String): Result<List<Country>> {
        return try {
            val response = restCountriesService.getCountriesByRegion(region)
            if (response.isSuccessful && response.body() != null) {
                val countries = response.body()!!
                    .filter { !it.capital.isNullOrEmpty() }
                    .sortedBy { it.name.common }
                Result.success(countries)
            } else {
                Result.failure(Exception("Error al cargar países de la región: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Función para obtener el clima actual (Usado en CountryDetailActivity)
    suspend fun getCurrentWeather(capital: String): Result<WeatherResponse> {
        return try {
            val response = weatherService.getCurrentWeather(capital = capital)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                // Manejo de error específico (Token, Ciudad no encontrada, etc.)
                val errorMsg = when (response.code()) {
                    401 -> "Error 401: Token/API Key inválido o expirado." // Autenticación
                    400 -> "Error 400: Ciudad '$capital' no encontrada o petición inválida." // Ciudad no encontrada
                    else -> "Error ${response.code()}: No se pudo obtener el clima."
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            // Manejo de error de red (Timeout)
            Result.failure(Exception("Error de conexión al obtener el clima: ${e.message}"))
        }
    }
}