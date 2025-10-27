package com.example.countryweatherapp.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.countryweatherapp.R
import com.example.countryweatherapp.model.Country

class CountryAdapter(
    private val countries: List<Country>,
    private val onCountryClick: (Country) -> Unit
) : RecyclerView.Adapter<CountryAdapter.CountryViewHolder>() {

    // Clase ViewHolder que contiene las referencias a las vistas de cada elemento de la lista.
    class CountryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageViewFlag: ImageView = view.findViewById(R.id.imageViewFlag)
        val textViewCountryName: TextView = view.findViewById(R.id.textViewCountryName)
        val textViewCountryCapital: TextView = view.findViewById(R.id.textViewCountryCapital)
    }

    // Crea nuevos ViewHolders (llamado por el LayoutManager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_country, parent, false)
        return CountryViewHolder(view)
    }

    // Reemplaza el contenido de una vista (llamado por el LayoutManager)
    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        val country = countries[position]

        // 1. Mostrar Nombre y Capital
        holder.textViewCountryName.text = country.name.common
        // Acceder al primer elemento de la lista de capitales (o N/A si está vacía/nula)
        val capitalName = country.capital?.firstOrNull() ?: "N/A"
        holder.textViewCountryCapital.text = "Capital: $capitalName"

        // 2. Cargar Bandera usando Coil
        // Coil se encarga de la carga de imágenes.
        holder.imageViewFlag.load(country.flags.png) {
            crossfade(true)
            placeholder(R.drawable.ic_launcher_background) // Placeholder mientras carga
            error(R.drawable.ic_launcher_background) // Imagen si falla
        }

        // 3. Manejar el evento de clic
        holder.itemView.setOnClickListener {
            onCountryClick(country)
        }
    }

    // Retorna el tamaño total de la lista
    override fun getItemCount() = countries.size
}