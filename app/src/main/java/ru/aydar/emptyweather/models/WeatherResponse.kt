package ru.aydar.emptyweather.models

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class WeatherResponse(
        @SerializedName("list")
        var list: List<WeatherResp>
) {
    @Entity(tableName = "weathers")
    data class WeatherResp(
            @SerializedName("id")
            @PrimaryKey
            var id: Int? = null,
            @SerializedName("name")
            var name: String? = null,
            @SerializedName("coord")
            @Ignore
            var coord: Coord? = null,
            @SerializedName("main")
            @Embedded(prefix = "main")
            var main: Main? = null,
            @SerializedName("dt")
            @Ignore
            var dt: Int? = null,
            @SerializedName("wind")
            @Embedded(prefix = "wind")
            var wind: Wind? = null,
            @SerializedName("sys")
            @Embedded(prefix = "sys")
            var sys: Sys? = null,
            @SerializedName("rain")
            @Ignore
            var rain: Any? = null,
            @SerializedName("snow")
            @Ignore
            var snow: Any? = null,
            @SerializedName("clouds")
            @Ignore
            var clouds: Clouds? = null,
            @SerializedName("weather")
            @Ignore
            var weather: List<Weather>? = null
    ) {
        override fun equals(other: Any?): Boolean {
            if (!super.equals(other)) return false
            if (this === other) return true
            val otherObj: WeatherResp = other as WeatherResp
            return (this.id == otherObj.id &&
                    this.name.equals(otherObj.name)
                    )

        }
    }
}