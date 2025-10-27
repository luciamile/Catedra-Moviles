package com.example.countryweatherapp.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.countryweatherapp.R
import com.example.countryweatherapp.controller.CountryRepository
import com.example.countryweatherapp.model.Country
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CountryListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var textViewError: TextView // Nueva variable para el TextView de error
    private val repository = CountryRepository()
    private var regionName: String? = null

    companion object {
        const val EXTRA_REGION_NAME = "extra_region_name"
        const val EXTRA_COUNTRY_NAME = "extra_country_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_country_list)

        // Obtener el nombre de la región pasada desde MainActivity
        regionName = intent.getStringExtra(EXTRA_REGION_NAME)
        title = "Países en: $regionName" // Título de la ActionBar

        recyclerView = findViewById(R.id.recyclerViewCountries)
        progressBar = findViewById(R.id.progressBarCountryList)
        textViewError = findViewById(R.id.textViewError) // Inicializar el TextView de error
        recyclerView.layoutManager = LinearLayoutManager(this)

        if (regionName != null) {
            loadCountries(regionName!!)
        } else {
            Toast.makeText(this, "Región no especificada.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun loadCountries(region: String) {
        progressBar.visibility = View.VISIBLE // Indicador de carga
        textViewError.visibility = View.GONE // Ocultar errores al iniciar la carga

        CoroutineScope(Dispatchers.IO).launch {
            val result = repository.getCountriesByRegion(region)

            withContext(Dispatchers.Main) {
                progressBar.visibility = View.GONE // Ocultar indicador
                result.onSuccess { countries ->
                    if (countries.isEmpty()) {
                        // Manejo de caso donde no hay países para la región (o todos fueron filtrados)
                        textViewError.text = "No se encontraron países con datos de capital en esta región."
                        textViewError.visibility = View.VISIBLE
                    } else {
                        // Ocultar error y mostrar lista si hay datos
                        textViewError.visibility = View.GONE
                        // Configurar el adaptador con la lista de países
                        recyclerView.adapter = CountryAdapter(countries) { country ->
                            // Navegación a CountryDetailActivity
                            val intent = Intent(this@CountryListActivity, CountryDetailActivity::class.java).apply {
                                putExtra(EXTRA_COUNTRY_NAME, country.name.common)
                            }
                            startActivity(intent)
                        }
                    }
                }.onFailure { e ->
                    // Manejo de error amigable: Muestra el mensaje en el TextView
                    textViewError.text = "Error de carga: ${e.message}. Verifique su conexión."
                    textViewError.visibility = View.VISIBLE
                }
            }
        }
    }
}