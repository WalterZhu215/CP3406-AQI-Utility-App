package com.jcu.cp3406.cp3406assignment1aqi.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://air-quality-api.open-meteo.com/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val aqiApi: AqiApi by lazy {
        retrofit.create(AqiApi::class.java)
    }
}