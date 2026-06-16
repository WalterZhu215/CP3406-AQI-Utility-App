package com.jcu.cp3406.cp3406assignment1aqi.data.repository

import com.jcu.cp3406.cp3406assignment1aqi.data.api.RetrofitClient
import com.jcu.cp3406.cp3406assignment1aqi.data.model.AqiCurrent
import com.jcu.cp3406.cp3406assignment1aqi.data.model.AqiResponse
import retrofit2.Call

class AqiRepository {

    fun fetchAirQuality(lat: Double, lon: Double): Call<AqiResponse> {
        return RetrofitClient.aqiApi.getAirQuality(latitude = lat, longitude = lon)
    }

    fun getMockAqiData(): AqiResponse {
        return AqiResponse(
            current = AqiCurrent(
                european_aqi = 42,
                pm2_5 = 12.5f,
                pm10 = 28.3f,
                o3 = 65.2f
            )
        )
    }
}