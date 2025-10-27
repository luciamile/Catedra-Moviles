package com.example.countryweatherapp.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.countryweatherapp.R

class RegionAdapter(
    private val regions: List<String>,
    private val onRegionClick: (String) -> Unit // Manejar navegación al hacer clic [cite: 31]
) : RecyclerView.Adapter<RegionAdapter.RegionViewHolder>() {

    class RegionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewRegionName: TextView = view.findViewById(R.id.textViewRegionName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_region, parent, false)
        return RegionViewHolder(view)
    }

    override fun onBindViewHolder(holder: RegionViewHolder, position: Int) {
        val region = regions[position]
        holder.textViewRegionName.text = region
        holder.itemView.setOnClickListener {
            onRegionClick(region)
        }
    }

    override fun getItemCount() = regions.size
}