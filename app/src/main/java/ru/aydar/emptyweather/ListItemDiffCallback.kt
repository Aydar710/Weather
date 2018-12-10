package ru.aydar.emptyweather

import android.support.v7.util.DiffUtil
import ru.aydar.emptyweather.models.WeatherResponse

class ListItemDiffCallback : DiffUtil.ItemCallback<WeatherResponse.WeatherResp>() {
    override fun areItemsTheSame(p0: WeatherResponse.WeatherResp, p1: WeatherResponse.WeatherResp): Boolean {
        return p0.id == p1.id
    }

    override fun areContentsTheSame(p0: WeatherResponse.WeatherResp, p1: WeatherResponse.WeatherResp): Boolean {
        return p0 == p1
    }
}