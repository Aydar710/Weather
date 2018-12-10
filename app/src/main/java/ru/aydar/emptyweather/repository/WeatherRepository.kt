package ru.aydar.emptyweather.repository

import android.util.Log
import io.reactivex.Observer
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.aydar.emptyweather.WeatherApi
import ru.aydar.emptyweather.constants.COUNT_CITY_20
import ru.aydar.emptyweather.models.Coord
import ru.aydar.emptyweather.models.WeatherData
import ru.aydar.emptyweather.models.WeatherResponse

class WeatherRepository(val weatherApi: WeatherApi) {

    fun getCitiesInCycle(coord: Coord): Single<List<WeatherResponse.WeatherResp>> {
        Log.i("weather", "Getting cities in cycle")
        return weatherApi.getCitiesInCycle(coord.lat.toString(), coord.lon.toString(), COUNT_CITY_20)
                .map { it.list }
                .map { list ->
                    list.forEach {
                        it.main?.temp = it.main?.temp
                    }
                    list
                }
                .subscribeOn(Schedulers.io())
    }

    fun getCityById(id: Int): Single<WeatherData> {
        return weatherApi.getWeatherByCityId(id.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}