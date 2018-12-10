package ru.aydar.emptyweather.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_weather_details.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.aydar.emptyweather.R
import ru.aydar.emptyweather.Retrofit
import ru.aydar.emptyweather.constants.LAT_KZN
import ru.aydar.emptyweather.constants.LOCATION_PERMISSION_CODE
import ru.aydar.emptyweather.constants.LON_KZN
import ru.aydar.emptyweather.constants.CITY_ID
import ru.aydar.emptyweather.database.WeatherRespDb
import ru.aydar.emptyweather.models.Coord
import ru.aydar.emptyweather.models.WeatherData
import ru.aydar.emptyweather.models.WeatherResponse
import ru.aydar.emptyweather.repository.WeatherRepository

class WeatherDetailsActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var coord: Coord

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_details)

        val mRetrofit = Retrofit.instance
        val weatherRepository = WeatherRepository(mRetrofit.getWeatherService(this))

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        coord = Coord()
        coord.lat = java.lang.Double.parseDouble(LAT_KZN)
        coord.lon = java.lang.Double.parseDouble(LON_KZN)

        checkLocationPermission()
        val db = WeatherRespDb.getInstance(applicationContext).getListElementDao()

        if (MainActivity.hasConnection(this)) {
            weatherRepository.getCityById(intent.getIntExtra(CITY_ID, 0))
                    .subscribe({ weatherData ->
                        bindWeatherData(weatherData)
                    },
                            { it.printStackTrace() }
                    )
        } else {
            var weather: WeatherResponse.WeatherResp? = null
            GlobalScope.launch {
                weather = db.getWeatherById(intent.getIntExtra(CITY_ID, 0))
            }
            bindWeatherResp(weather)
        }
    }

    private fun convertDegreesToDirection(deg: Int?) =
            when (deg) {
                in 0..44 -> "Северо-Северо-Восток"
                45 -> "Северо-Восток"
                in 45..89 -> "Восток-Северо-Восток"
                90 -> "Восток"
                in 90..134 -> "Восток-Юго-Восток"
                135 -> "Юго-Восток"
                in 135..179 -> "Юго-Юго_Восток"
                in 180..224 -> "Юго-Запад"
                in 225..269 -> "Запад"
                in 270..314 -> "Северо-Запад"
                in 315..360 -> "Север"
                else -> "Непонятно откуда"
            }


    fun Int.convertToMmOfMercury(): Double {
        return this.div(1.333)
    }

    fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.i("weather", "Already granted")
            fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        coord.lat = location?.latitude
                        coord.lon = location?.longitude
                    }
        } else {
            requestLocationPermission()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_LONG).show()
            } else
                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed to read storage")
                    .setPositiveButton("ok") { dialog, which ->
                        ActivityCompat.requestPermissions(this@WeatherDetailsActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_CODE)
                        //here
                        fusedLocationClient.lastLocation
                                .addOnSuccessListener { location: Location? ->
                                    coord.lat = location?.latitude
                                    coord.lon = location?.longitude
                                }
                    }
                    .setNegativeButton("cancel") { dialog, which -> dialog?.dismiss() }
                    .create().show()
        } else {
            ActivityCompat.requestPermissions(this@WeatherDetailsActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_CODE)
        }
    }

    private fun bindWeatherData(weatherData: WeatherData) {
        txt_details_city.text = weatherData.name
        txt_details_country.text = weatherData.sys?.country
        txt_details_temp.text = "${weatherData.main?.temp} °C"
        txt_details_humidity.text = getString(R.string.humidity, weatherData.main?.humidity.toString())
        txt_details_pressure.text = getString(R.string.pressure, weatherData.main?.pressure?.convertToMmOfMercury().toString())
        txt_details_direction.text = getString(R.string.wind_direction, convertDegreesToDirection(weatherData.wind?.deg))
    }

    private fun bindWeatherResp(weatherResp: WeatherResponse.WeatherResp?) {
        txt_details_city.text = weatherResp?.name
        txt_details_country.text = weatherResp?.sys?.country
        txt_details_temp.text = "${weatherResp?.main?.temp} °C"
        txt_details_humidity.text = getString(R.string.humidity, weatherResp?.main?.humidity.toString())
        txt_details_pressure.text = getString(R.string.pressure, weatherResp?.main?.pressure?.convertToMmOfMercury().toString())
        txt_details_direction.text = getString(R.string.wind_direction, convertDegreesToDirection(weatherResp?.wind?.deg))
    }
}
