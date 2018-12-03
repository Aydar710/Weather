package ru.aydar.emptyweather.activities

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_weather_details.*
import ru.aydar.emptyweather.Constants
import ru.aydar.emptyweather.Constants.Companion.LOCATION_PERMISSION_CODE
import ru.aydar.emptyweather.R
import ru.aydar.emptyweather.Retrofit
import ru.aydar.emptyweather.models.Coord
import ru.aydar.emptyweather.models.WeatherResponse
import ru.aydar.emptyweather.repository.WeatherRepository

class WeatherDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_details)

        val position = intent.getIntExtra(Constants.POSITION, 0)
        val weatherRepository = WeatherRepository(Retrofit.getInstance(this))

        var fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        var coord = Coord()
        coord.lat = java.lang.Double.parseDouble(Constants.LAT_KZN)
        coord.lon = java.lang.Double.parseDouble(Constants.LON_KZN)

        checkLocationPermission()
        fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    coord.lat = location?.latitude
                    coord.lon = location?.longitude
                }

        weatherRepository.getCitiesInCycle(coord).subscribe(
                { list ->
                    val element: WeatherResponse.ListElement = list[position]
                    txt_details_city.text = element.name
                    txt_details_country.text = element.sys?.country
                    txt_details_temp.text = "${element.main?.temp} °C"
                    txt_details_humidity.text = "Влажность: ${element.main?.humidity} %"
                    txt_details_pressure.text = "Давление: ${convertHectoPaskalToMillimeterOfMercury(element.main?.pressure)} мм.рт.ст"
                    txt_details_direction.text = "Направление ветра: ${convertDegreesToDirection(element.wind?.deg)}"
                },
                {
                    it.printStackTrace()
                }
        )
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

    private fun convertHectoPaskalToMillimeterOfMercury(hPa: Int?) = hPa?.div(1.333)

    fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.i("weather", "Already granted")
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

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed to read storage")
                    .setPositiveButton("ok") { dialog, which -> ActivityCompat.requestPermissions(this@WeatherDetailsActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_CODE) }.setNegativeButton("cancel") { dialog, which -> dialog?.dismiss() }.create().show()
        } else {
            ActivityCompat.requestPermissions(this@WeatherDetailsActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_CODE)
        }
    }
}
