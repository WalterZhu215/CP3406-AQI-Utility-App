package com.jcu.cp3406.cp3406assignment1aqi.data.api

import com.jcu.cp3406.cp3406assignment1aqi.data.model.AqiResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface AqiApi {

    @GET("v1/air-quality")
    fun getAirQuality(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") currentMetrics: String = "european_aqi,pm2_5,pm10,ozone"
    ): Call<AqiResponse>
}