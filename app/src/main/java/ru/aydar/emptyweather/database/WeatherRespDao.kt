package ru.aydar.emptyweather.database

import android.arch.persistence.room.*
import io.reactivex.Flowable
import ru.aydar.emptyweather.models.WeatherResponse

@Dao
interface WeatherRespDao {


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(listEl: WeatherResponse.WeatherResp)

    @Update
    fun update(listEl: WeatherResponse.WeatherResp)

    @Delete
    fun delete(listEl: WeatherResponse.WeatherResp)

    @Query("SELECT * FROM weathers WHERE id = :id")
    fun getById(id: Int): Flowable<List<WeatherResponse.WeatherResp>>

    @Query("SELECT * FROM weathers")
    fun getAll(): Flowable<List<WeatherResponse.WeatherResp>>

    @Query("SELECT * FROM weathers")
    fun gatAllAsList(): List<WeatherResponse.WeatherResp>


    @Query("SELECT * FROM weathers WHERE id = :id")
    fun getWeatherById(id: Int): WeatherResponse.WeatherResp

    @Query("DELETE FROM weathers")
    fun clear()

}