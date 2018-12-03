package ru.aydar.emptyweather.activities

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*
import ru.aydar.emptyweather.R
import ru.aydar.emptyweather.models.Coord
import ru.aydar.emptyweather.models.WeatherResponse
import ru.aydar.emptyweather.repository.WeatherRepository
import ru.aydar.weatherexample.Constants
import ru.aydar.weatherexample.Constants.Companion.LOCATION_PERMISSION_CODE
import ru.aydar.weatherexample.Retrofit
import ru.aydar.weatherexample.WeatherAdapter

class MainActivity : AppCompatActivity(), WeatherAdapter.Listener {

    private lateinit var weatherRepository: WeatherRepository
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        weatherRepository = WeatherRepository(Retrofit.getInstance(this))

        checkLocationPermission()
        var coord = Coord()
        coord.lat = java.lang.Double.parseDouble(Constants.LAT_KZN)
        coord.lon = java.lang.Double.parseDouble(Constants.LON_KZN)

        fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    coord.lat = location?.latitude
                    coord.lon = location?.longitude
                }

        val recyclerView = rv_weather
        val adapter = WeatherAdapter(null)
        weatherRepository.getCitiesInCycle(coord).subscribe(
                { list ->
                    adapter.submitList(list as ArrayList<WeatherResponse.ListElement>)
                },
                {
                    it.printStackTrace()
                    print("ERROR")
                })
        adapter.setListener(this)
        recyclerView.adapter = adapter
    }

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

    override fun onClick(position: Int) {
        val intent = Intent(this, WeatherDetailsActivity::class.java)
        intent.putExtra(Constants.POSITION, position)
        startActivity(intent)
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed to read storage")
                    .setPositiveButton("ok") { dialog, which -> ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_CODE) }.setNegativeButton("cancel", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            dialog?.dismiss()
                        }
                    }).create().show()


        } else {
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_CODE)
        }
    }
}
