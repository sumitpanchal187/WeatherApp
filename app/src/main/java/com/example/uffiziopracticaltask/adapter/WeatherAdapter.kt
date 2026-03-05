package com.example.uffiziopracticaltask.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uffiziopracticaltask.R
import com.example.uffiziopracticaltask.room.entity.WeatherEntity
import com.squareup.picasso.Picasso

class WeatherAdapter(
    private val list: List<WeatherEntity>
) : RecyclerView.Adapter<WeatherAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {

        val date: TextView = view.findViewById(R.id.txtDate)
        val temp: TextView = view.findViewById(R.id.txtTemp)
        val condition: TextView = view.findViewById(R.id.txtCondition)
        val icon: ImageView = view.findViewById(R.id.imgWeather)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_weather, parent, false)

        return VH(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: VH, position: Int) {

        val item = list[position]

        holder.date.text = item.date
        holder.temp.text = "${item.temperature} °C"
        holder.condition.text = item.condition

            val url = "https://openweathermap.org/img/wn/${item.icon}@2x.png"

        Picasso.get().load(url).into(holder.icon)
    }
}