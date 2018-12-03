package ru.aydar.emptyweather.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class WeatherResponse(
        @SerializedName("list")
        var list: List<ListElement>
) {

    data class ListElement(
            @SerializedName("id")
            var id: Int? = null,
            @SerializedName("name")
            var name: String? = null,
            @SerializedName("coord")
            var coord: Coord? = null,
            @SerializedName("main")
            var main: Main? = null,
            @SerializedName("dt")
            var dt: Int? = null,
            @SerializedName("wind")
            var wind: Wind? = null,
            @SerializedName("sys")
            var sys: Sys? = null,
            @SerializedName("rain")
            var rain: Any? = null,
            @SerializedName("snow")
            var snow: Any? = null,
            @SerializedName("clouds")
            var clouds: Clouds? = null,
            @SerializedName("weather")
            var weather: List<Weather>? = null
    )
}