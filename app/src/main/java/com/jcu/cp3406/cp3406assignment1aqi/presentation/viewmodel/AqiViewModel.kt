package com.jcu.cp3406.cp3406assignment1aqi.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jcu.cp3406.cp3406assignment1aqi.data.model.AqiResponse
import com.jcu.cp3406.cp3406assignment1aqi.data.repository.AqiRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AqiViewModel(
    private val repository: AqiRepository
) : ViewModel() {

    val aqiData = MutableLiveData<AqiResponse?>()
    val isLoading = MutableLiveData(false)
    val errorMessage = MutableLiveData("")

    val currentCity = MutableLiveData("Melbourne")
    val showDetailedPollutants = MutableLiveData(true)
    val useChineseStandard = MutableLiveData(false)
    val lastUpdatedTime = MutableLiveData("")

    // 扩充：内置全球主要核心城市的坐标库
    private val cityCoordinates = mapOf(
        // Oceania
        "Melbourne" to Pair(-37.81, 144.96),
        "Sydney" to Pair(-33.87, 151.21),
        "Brisbane" to Pair(-27.47, 153.03),
        "Perth" to Pair(-31.95, 115.86),
        "Auckland" to Pair(-36.85, 174.76),

        // Asia
        "Beijing" to Pair(39.90, 116.40),
        "Shanghai" to Pair(31.23, 121.47),
        "Guangzhou" to Pair(23.12, 113.26),
        "Tokyo" to Pair(35.67, 139.65),
        "Seoul" to Pair(37.56, 126.97),
        "Singapore" to Pair(1.35, 103.81),
        "Bangkok" to Pair(13.75, 100.50),
        "Mumbai" to Pair(19.07, 72.87),
        "Dubai" to Pair(25.20, 55.27),

        // Europe
        "London" to Pair(51.50, -0.12),
        "Paris" to Pair(48.85, 2.35),
        "Berlin" to Pair(52.52, 13.40),
        "Rome" to Pair(41.90, 12.49),
        "Madrid" to Pair(40.41, -3.70),
        "Moscow" to Pair(55.75, 37.61),

        // Americas
        "New York" to Pair(40.71, -74.00),
        "Los Angeles" to Pair(34.05, -118.24),
        "Toronto" to Pair(43.65, -79.38),
        "Vancouver" to Pair(49.28, -123.12),
        "Mexico City" to Pair(19.43, -99.13),
        "São Paulo" to Pair(-23.55, -46.63),

        // Africa
        "Cairo" to Pair(30.04, 31.23),
        "Cape Town" to Pair(-33.92, 18.42)
    )

    val availableCities = cityCoordinates.keys.toList()

    fun loadAqiData() {
        isLoading.postValue(true)
        errorMessage.postValue("")

        val city = currentCity.value ?: "Melbourne"
        val (lat, lon) = cityCoordinates[city] ?: cityCoordinates["Melbourne"]!!

        repository.fetchAirQuality(lat, lon).enqueue(object : Callback<AqiResponse> {
            override fun onResponse(call: Call<AqiResponse>, response: Response<AqiResponse>) {
                isLoading.postValue(false)
                updateTimestamp()

                if (response.isSuccessful && response.body() != null) {
                    aqiData.postValue(response.body())
                } else {
                    aqiData.postValue(repository.getMockAqiData())
                    errorMessage.postValue("API unavailable, showing demo data")
                }
            }

            override fun onFailure(call: Call<AqiResponse>, t: Throwable) {
                isLoading.postValue(false)
                updateTimestamp()
                aqiData.postValue(repository.getMockAqiData())
                errorMessage.postValue("Network error, showing demo data")
            }
        })
    }

    private fun updateTimestamp() {
        val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        lastUpdatedTime.postValue(currentTime)
    }

    fun switchCity(cityName: String) {
        currentCity.postValue(cityName)
        loadAqiData()
    }

    fun toggleDetailedPollutants(show: Boolean) {
        showDetailedPollutants.postValue(show)
    }

    fun toggleChineseStandard(useChinese: Boolean) {
        useChineseStandard.postValue(useChinese)
    }
}