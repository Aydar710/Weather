package ru.aydar.emptyweather

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.card_weather.view.*
import ru.aydar.emptyweather.models.WeatherResponse

class WeatherAdapter(var weatherList: ArrayList<WeatherResponse.ListElement>?): RecyclerView.Adapter<WeatherAdapter.ViewHolder>() {

    private lateinit var listener: Listener
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_weather, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        if (weatherList != null)
            return weatherList!!.size
        else return 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listElement: WeatherResponse.ListElement = weatherList?.get(position)!!
        holder.txtTemp.text = listElement.main?.temp.toString()
        holder.txtCity.text = listElement.name
        holder.txtCountry.text = listElement.sys?.country
        holder.itemView.setOnClickListener {
            listener.onClick(position)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtTemp = itemView.txt_temp
        var txtCountry = itemView.txt_country
        var txtCity = itemView.txt_city
    }

    fun submitList(newList: ArrayList<WeatherResponse.ListElement>) {
        weatherList = newList
        notifyDataSetChanged()
    }

    interface Listener {
        fun onClick(position: Int)
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }
}