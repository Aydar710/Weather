package ru.aydar.emptyweather

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.card_weather.view.*
import ru.aydar.emptyweather.models.WeatherResponse

class WeatherAdapter(private val listener: ListItemClickListener) : ListAdapter<
        WeatherResponse.WeatherResp,
        WeatherAdapter.ViewHolder>(ListItemDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_weather, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listElement = getItem(position)
        listElement?.let {
            holder.bind(it, it.id)
        }
    }

    inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        var txtTemp = containerView.txt_temp
        var txtCountry = containerView.txt_country
        var txtCity = containerView.txt_city

        fun bind(item: WeatherResponse.WeatherResp, id: Int?) {
            txtTemp.text = item.main?.temp.toString()
            txtCity.text = item.name
            txtCountry.text = item.sys?.country

            containerView.setOnClickListener {
                id?.let {
                    listener.onItemClick(it)
                }
            }
        }
    }

    interface ListItemClickListener {
        fun onItemClick(id: Int)
    }

}