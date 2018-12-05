package ru.aydar.emptyweather

import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query
import ru.aydar.emptyweather.models.WeatherData
import ru.aydar.emptyweather.models.WeatherResponse

interface WeatherApi {

    @GET("weather")
    fun getWeatherByLatLng(
            @Query("lat") lat: String,
            @Query("lon") lng: String,
            @Query("units") unit: String)
            : Observable<WeatherData>


    @GET("weather")
    fun getWeatherByCityName(@Query("q") cityName: String): Single<WeatherData>

    @GET("weather?")
    fun getWeatherByCityId(@Query("id") id: String): Single<WeatherData>

    @GET("find")
    fun getCitiesInCycle(
            @Query("lat") lat: String,
            @Query("lon") lon: String,
            @Query("cnt") count: String
    ): Single<WeatherResponse>
}