package com.example.countryweatherapp.view

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.example.countryweatherapp.R
import com.example.countryweatherapp.controller.CountryRepository
import com.example.countryweatherapp.model.WeatherResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CountryDetailActivity : AppCompatActivity() {

    // Vistas del país (detalle del país completo)
    private lateinit var scrollViewDetail: ScrollView
    private lateinit var detailProgressBar: ProgressBar
    private lateinit var detailImageViewFlag: ImageView
    private lateinit var detailTextViewCountryName: TextView
    private lateinit var detailTextViewRegion: TextView
    private lateinit var detailTextViewCapital: TextView
    private lateinit var detailTextViewPopulation: TextView

    // Vistas del clima (clima actual de la capital)
    private lateinit var weatherTextViewError: TextView
    private lateinit var weatherImageViewCondition: ImageView
    private lateinit var weatherTextViewTemp: TextView
    private lateinit var weatherTextViewCondition: TextView
    private lateinit var weatherTextViewWindHumidity: TextView

    private val repository = CountryRepository()
    private var countryName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_country_detail)

        // Inicializar Vistas (Binding manual)
        initializeViews()

        countryName = intent.getStringExtra(CountryListActivity.EXTRA_COUNTRY_NAME)

        if (countryName != null) {
            title = countryName
            loadCountryDetail(countryName!!)
        } else {
            Toast.makeText(this, "País no especificado.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun initializeViews() {
        scrollViewDetail = findViewById(R.id.scrollViewDetail)
        detailProgressBar = findViewById(R.id.detailProgressBar)

        detailImageViewFlag = findViewById(R.id.detailImageViewFlag)
        detailTextViewCountryName = findViewById(R.id.detailTextViewCountryName)
        detailTextViewRegion = findViewById(R.id.detailTextViewRegion)
        detailTextViewCapital = findViewById(R.id.detailTextViewCapital)
        detailTextViewPopulation = findViewById(R.id.detailTextViewPopulation)

        weatherTextViewError = findViewById(R.id.weatherTextViewError)
        weatherImageViewCondition = findViewById(R.id.weatherImageViewCondition)
        weatherTextViewTemp = findViewById(R.id.weatherTextViewTemp)
        weatherTextViewCondition = findViewById(R.id.weatherTextViewCondition)
        weatherTextViewWindHumidity = findViewById(R.id.weatherTextViewWindHumidity)
    }

    private fun loadCountryDetail(name: String) {
        // Ocultar vistas de clima al inicio de la carga para una UX limpia
        weatherTextViewError.visibility = View.GONE
        weatherTextViewTemp.visibility = View.GONE
        weatherTextViewCondition.visibility = View.GONE
        weatherTextViewWindHumidity.visibility = View.GONE
        detailProgressBar.visibility = View.VISIBLE // Indicador de carga visible

        CoroutineScope(Dispatchers.IO).launch {
            // LÓGICA CORREGIDA: Usamos la función del Repositorio que devuelve todos los países
            val result = repository.getAllCountriesList()

            withContext(Dispatchers.Main) {
                detailProgressBar.visibility = View.GONE // Ocultar indicador

                result.onSuccess { allCountries ->
                    // Filtrar localmente el país por el nombre común
                    val country = allCountries.find { it.name.common == name }

                    if (country != null) {
                        // Cargar y mostrar datos del país (Rest Countries)
                        renderCountryDetails(country)

                        // Cargar el clima
                        val capital = country.capital?.firstOrNull()
                        if (capital != null) {
                            loadWeather(capital)
                        } else {
                            weatherTextViewError.text = "Capital no disponible para el clima."
                            weatherTextViewError.visibility = View.VISIBLE
                        }
                    } else {
                        Toast.makeText(this@CountryDetailActivity, "País no encontrado.", Toast.LENGTH_LONG).show()
                        finish()
                    }
                }.onFailure { e ->
                    // Manejo de error amigable
                    Toast.makeText(this@CountryDetailActivity, "Error al cargar el detalle del país: ${e.message}", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }
    }

    private fun renderCountryDetails(country: com.example.countryweatherapp.model.Country) {
        detailImageViewFlag.load(country.flags.png)
        detailTextViewCountryName.text = country.name.official
        detailTextViewRegion.text = "Región: ${country.region} (${country.subregion ?: "N/A"})"
        detailTextViewCapital.text = "Capital: ${country.capital?.firstOrNull() ?: "N/A"}"
        detailTextViewPopulation.text = "Población: ${String.format("%,d", country.population)}"
        // TODO: Agregar más datos esperados: códigos ISO, monedas, idiomas, latlng
    }

    private fun loadWeather(capital: String) {
        // Ocultar errores anteriores
        weatherTextViewError.visibility = View.GONE

        CoroutineScope(Dispatchers.IO).launch {
            val result = repository.getCurrentWeather(capital)

            withContext(Dispatchers.Main) {
                result.onSuccess { weather ->
                    renderWeatherDetails(weather)
                }.onFailure { e ->
                    // Manejo de errores de WeatherAPI (token inválido, ciudad no encontrada)
                    weatherTextViewError.text = "Error del clima: ${e.message}"
                    weatherTextViewError.visibility = View.VISIBLE
                    // Ocultar vistas de clima si hay error
                    weatherTextViewTemp.visibility = View.GONE
                    weatherTextViewCondition.visibility = View.GONE
                    weatherTextViewWindHumidity.visibility = View.GONE
                }
            }
        }
    }

    private fun renderWeatherDetails(weather: WeatherResponse) {
        // Mostrar vistas del clima si la carga es exitosa
        weatherTextViewError.visibility = View.GONE
        weatherTextViewTemp.visibility = View.VISIBLE
        weatherTextViewCondition.visibility = View.VISIBLE
        weatherTextViewWindHumidity.visibility = View.VISIBLE

        val current = weather.current
        // Datos esperados: temperatura °C/°F, condición, velocidad del viento (kph/mph), humedad, icono de estado.
        weatherTextViewTemp.text = String.format("%.1f°C / %.1f°F", current.tempC, current.tempF)
        weatherTextViewCondition.text = current.condition.text
        weatherTextViewWindHumidity.text = String.format("Viento: %.1f kph | Humedad: %d%%", current.windKph, current.humidity)

        // El icono de estado es una URL. WeatherAPI devuelve URLs con '//' que Coil/Glide maneja bien.
        weatherImageViewCondition.load("https:${current.condition.icon}")
    }
}