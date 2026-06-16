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
    val european_aqi: Int,   // European AQI standard index
    val pm2_5: Float,        // PM2.5 concentration (μg/m³)
    val pm10: Float,         // PM10 concentration (μg/m³)
    val o3: Float            // Ozone concentration (μg/m³)
)