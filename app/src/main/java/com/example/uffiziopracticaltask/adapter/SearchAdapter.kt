package com.example.uffiziopracticaltask.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uffiziopracticaltask.R

class SearchAdapter(
    private val list: List<String>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<SearchAdapter.CityVH>() {

    class CityVH(view: View) : RecyclerView.ViewHolder(view) {

        val cityText: TextView = view.findViewById(R.id.txtCity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityVH {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_city, parent, false)

        return CityVH(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: CityVH, position: Int) {

        val city = list[position]

        holder.cityText.text = city

        holder.itemView.setOnClickListener {

            onClick(city)
        }
    }
}