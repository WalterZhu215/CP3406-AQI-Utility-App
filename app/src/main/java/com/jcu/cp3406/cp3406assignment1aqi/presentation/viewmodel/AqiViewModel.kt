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

/**
 * ViewModel responsible for managing UI state and fetching Air Quality Index (AQI) data.
 * It interacts with the AqiRepository to handle network requests and exposes data via LiveData.
 * This ensures the UI survives configuration changes (like screen rotations).
 */
class AqiViewModel(
    private val repository: AqiRepository
) : ViewModel() {

    // Core state variables observed by the UI
    val aqiData = MutableLiveData<AqiResponse?>()
    val isLoading = MutableLiveData(false)
    val errorMessage = MutableLiveData("")

    // User preference and dynamic UI states
    val currentCity = MutableLiveData("Melbourne")
    val showDetailedPollutants = MutableLiveData(true)
    val useChineseStandard = MutableLiveData(false)
    val lastUpdatedTime = MutableLiveData("")

    /**
     * A built-in dictionary of major global cities and their respective geographic coordinates (Latitude, Longitude).
     * Used as a reliable fallback since dynamic geocoding APIs are not implemented.
     */
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

    // Expose the list of available cities for the UI dropdown menu
    val availableCities = cityCoordinates.keys.toList()

    /**
     * Fetches AQI data for the currently selected city using Retrofit.
     * Handles network loading states and updates the timestamp upon completion.
     */
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
                    // Fallback to mock data if API limits are reached or response is invalid
                    aqiData.postValue(repository.getMockAqiData())
                    errorMessage.postValue("API unavailable, showing demo data")
                }
            }

            override fun onFailure(call: Call<AqiResponse>, t: Throwable) {
                isLoading.postValue(false)
                updateTimestamp()
                // Graceful degradation on network failure
                aqiData.postValue(repository.getMockAqiData())
                errorMessage.postValue("Network error, showing demo data")
            }
        })
    }

    /**
     * Updates the timestamp variable with the current local time in "hh:mm a" format.
     */
    private fun updateTimestamp() {
        val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        lastUpdatedTime.postValue(currentTime)
    }

    /**
     * Updates the selected city and immediately triggers a new network request.
     * @param cityName The target city name selected by the user.
     */
    fun switchCity(cityName: String) {
        currentCity.postValue(cityName)
        loadAqiData()
    }

    /**
     * Toggles the visibility of detailed pollutant cards (PM2.5, PM10, O3) on the main screen.
     */
    fun toggleDetailedPollutants(show: Boolean) {
        showDetailedPollutants.postValue(show)
    }

    /**
     * Switches the evaluation metric between standard AQI and the Chinese National Standard.
     */
    fun toggleChineseStandard(useChinese: Boolean) {
        useChineseStandard.postValue(useChinese)
    }
}