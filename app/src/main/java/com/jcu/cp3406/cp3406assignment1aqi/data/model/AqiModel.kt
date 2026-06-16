package com.jcu.cp3406.cp3406assignment1aqi.data.model

/**
 * Response structure from Open-Meteo Air Quality API
 */
data class AqiResponse(
    val current: AqiCurrent
)

/**
 * Real-time air quality indicator data fields
 */
data class AqiCurrent(
    val european_aqi: Int,
    val pm2_5: Float,
    val pm10: Float,
    val o3: Float
)