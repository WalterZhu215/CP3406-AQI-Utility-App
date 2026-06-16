package com.jcu.cp3406.cp3406assignment1aqi.data.model

import com.google.gson.annotations.SerializedName

/**
 * Response structure from Open-Meteo Air Quality API
 */
data class AqiResponse(
    val current: AqiCurrent
)

/**
 * Real-time air quality indicator data fields
 * Field names mapped to official Open-Meteo API response
 */
data class AqiCurrent(
    @SerializedName("european_aqi")
    val european_aqi: Int,
    @SerializedName("pm2_5")
    val pm2_5: Float,
    @SerializedName("pm10")
    val pm10: Float,
    @SerializedName("ozone")
    val o3: Float
)