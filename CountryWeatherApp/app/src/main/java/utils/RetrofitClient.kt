package com.example.countryweatherapp.utils

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private fun getRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Instancia para Rest Countries
    val restCountriesInstance: Retrofit by lazy {
        getRetrofit(Constants.RESTCOUNTRIES_BASE_URL)
    }

    // Instancia para Weather API
    val weatherApiInstance: Retrofit by lazy {
        getRetrofit(Constants.WEATHERAPI_BASE_URL)
    }
}