package ru.aydar.emptyweather.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
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
import android.widget.Toast
import com.amitshekhar.DebugDB
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*
import ru.aydar.emptyweather.R
import ru.aydar.emptyweather.Retrofit
import ru.aydar.emptyweather.WeatherAdapter
import ru.aydar.emptyweather.constants.LAT_KZN
import ru.aydar.emptyweather.constants.LOCATION_PERMISSION_CODE
import ru.aydar.emptyweather.constants.LON_KZN
import ru.aydar.emptyweather.constants.CITY_ID
import ru.aydar.emptyweather.database.WeatherRespDb
import ru.aydar.emptyweather.models.Coord
import ru.aydar.emptyweather.models.WeatherResponse
import ru.aydar.emptyweather.repository.WeatherRepository
import java.lang.Double
import android.net.ConnectivityManager
import android.net.NetworkInfo
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), WeatherAdapter.ListItemClickListener {


    private lateinit var weatherRepository: WeatherRepository
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var usersCoord: Coord

    companion object {
        fun hasConnection(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            var wifiInfo: NetworkInfo? = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            if (wifiInfo != null && wifiInfo.isConnected) {
                return true
            }
            wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
            if (wifiInfo != null && wifiInfo.isConnected) {
                return true
            }
            wifiInfo = cm.activeNetworkInfo
            return wifiInfo != null && wifiInfo.isConnected
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.i("weather", DebugDB.getAddressLog())

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val mRetrofit = Retrofit.instance
        weatherRepository = WeatherRepository(mRetrofit.getWeatherService(this))

        checkLocationPermission()
        usersCoord = Coord()
        usersCoord.lat = Double.parseDouble(LAT_KZN)
        usersCoord.lon = Double.parseDouble(LON_KZN)

        val recyclerView = rv_weather
        val adapter = WeatherAdapter(this)

        val db = WeatherRespDb.getInstance(applicationContext).getListElementDao()

        if (hasConnection(this)) {
            weatherRepository.getCitiesInCycle(usersCoord)
                    .map { list ->
                        db.clear()
                        list.forEach {
                            db.insert(it)
                        }
                        list
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { list ->
                                adapter.submitList(list as ArrayList<WeatherResponse.WeatherResp>)
                            },
                            {
                                it.printStackTrace()
                            }
                    )
        } else {
            GlobalScope.launch {
                adapter.submitList(db.gatAllAsList())
            }
        }
        recyclerView.adapter = adapter
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.i("weather", "Already granted")
            fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        usersCoord.lat = location?.latitude
                        usersCoord.lon = location?.longitude
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

    override fun onItemClick(id: Int) {
        val intent = Intent(this, WeatherDetailsActivity::class.java)
        intent.putExtra(CITY_ID, id)
        startActivity(intent)
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed to read storage")
                    .setPositiveButton("ok") { dialog, which ->
                        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_CODE)
                        fusedLocationClient.lastLocation
                                .addOnSuccessListener { location: Location? ->
                                    usersCoord.lat = location?.latitude
                                    usersCoord.lon = location?.longitude
                                }
                    }
                    .setNegativeButton("cancel", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            dialog?.dismiss()
                        }
                    }).create().show()
        } else {
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_CODE)
        }
    }
}
