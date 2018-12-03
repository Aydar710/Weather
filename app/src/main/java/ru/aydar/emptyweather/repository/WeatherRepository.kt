package ru.aydar.emptyweather.repository

import android.util.Log
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.aydar.emptyweather.WeatherApi
import ru.aydar.emptyweather.models.Coord
import ru.aydar.emptyweather.models.WeatherResponse

class WeatherRepository(val weatherApi: WeatherApi) {

    fun getCitiesInCycle(coord: Coord): Single<List<WeatherResponse.ListElement>> {
        var list: ArrayList<WeatherResponse.ListElement> = ArrayList()
        Log.i("weather", "Getting cities in cycle")

        return weatherApi.getCitiesInCycle(coord.lat.toString(), coord.lon.toString(), "20")
                .map { it.list }
                .map { list ->
                    for (el in list)
                        el.main?.temp = convertKelvinToCelsius(el.main?.temp)
                    list
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    private fun convertKelvinToCelsius(kelvin: Double?): Double {
        var celsius: Double = 0.0
        kelvin?.let {
            celsius = it - 273.15
        }
        return celsius
    }
}