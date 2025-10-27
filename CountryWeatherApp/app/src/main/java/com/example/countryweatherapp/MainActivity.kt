package com.example.countryweatherapp.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.countryweatherapp.R
import com.example.countryweatherapp.controller.CountryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// La actividad es parte de la Vista, pero actúa como un mini-Controlador de UI
class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private val repository = CountryRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerViewRegions)
        progressBar = findViewById(R.id.progressBar)
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadRegions()
    }

    private fun loadRegions() {
        progressBar.visibility = View.VISIBLE // Indicador de carga
        CoroutineScope(Dispatchers.IO).launch {
            val result = repository.getAllRegions()

            withContext(Dispatchers.Main) {
                progressBar.visibility = View.GONE // Ocultar indicador
                result.onSuccess { regions ->
                    // Configurar el adaptador con las regiones
                    recyclerView.adapter = RegionAdapter(regions) { region ->

                        // INICIO: Lógica de navegación actualizada (Paso 6.4)
                        val intent = Intent(this@MainActivity, CountryListActivity::class.java).apply {
                            // Pasar el nombre de la región al CountryListActivity
                            putExtra(CountryListActivity.EXTRA_REGION_NAME, region)
                        }
                        startActivity(intent)
                        // FIN: Lógica de navegación actualizada

                    }
                }.onFailure { e ->
                    // Manejo de error amigable [cite: 31, 42]
                    Toast.makeText(this@MainActivity, "Error al cargar regiones: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}