package com.jcu.cp3406.cp3406assignment1aqi.data.api

import com.jcu.cp3406.cp3406assignment1aqi.data.model.AqiResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit API interface for Open-Meteo Air Quality service
 * Defines HTTP request methods and parameters
 */
interface AqiApi {
    @GET("v1/air-quality")
    fun getAirQuality(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") currentParams: String = "european_aqi,pm2_5,pm10,o3"
    ): Call<AqiResponse>
}