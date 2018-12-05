package ru.aydar.emptyweather

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.card_weather.view.*
import ru.aydar.emptyweather.models.WeatherResponse

class WeatherAdapter(var weatherList: ArrayList<WeatherResponse.ListElement>?) : RecyclerView.Adapter<WeatherAdapter.ViewHolder>() {

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
        val listElement: WeatherResponse.ListElement? = weatherList?.get(position)
        listElement?.let {
            holder.bind(it)
        }
        holder.containerView.setOnClickListener {
            listener.onClick(listElement?.id)
        }
    }

    class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        var txtTemp = containerView.txt_temp
        var txtCountry = containerView.txt_country
        var txtCity = containerView.txt_city

        fun bind(item: WeatherResponse.ListElement) {
            txtTemp.text = item.main?.temp.toString()
            txtCity.text = item.name
            txtCountry.text = item.sys?.country
        }
    }

    fun submitList(newList: ArrayList<WeatherResponse.ListElement>) {
        weatherList = newList
        notifyDataSetChanged()
    }

    interface Listener {
        fun onClick(id: Int?)
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }
}