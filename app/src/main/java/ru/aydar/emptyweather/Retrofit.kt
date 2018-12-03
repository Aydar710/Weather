package ru.aydar.emptyweather

import android.content.Context
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.aydar.weatherexample.WeatherApi


class Retrofit {

    companion object {
        private lateinit var retrofit: Retrofit

        fun getInstance(context: Context): WeatherApi {
            Stetho.initializeWithDefaults(context)

            val okHttpClient = OkHttpClient.Builder()
                    .addNetworkInterceptor(StethoInterceptor())

            okHttpClient.interceptors().add(Interceptor { chain ->
                var request = chain.request()
                val url = request.url().newBuilder().addQueryParameter("appid", Constants.APPID).build()
                request = request.newBuilder().url(url).build()
                chain.proceed(request)
            })

            retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("http://api.openweathermap.org/data/2.5/")
                    .client(okHttpClient.build())
                    .build()

            val weatherService: WeatherApi = retrofit.create(WeatherApi::class.java)
            return weatherService
        }
    }
}