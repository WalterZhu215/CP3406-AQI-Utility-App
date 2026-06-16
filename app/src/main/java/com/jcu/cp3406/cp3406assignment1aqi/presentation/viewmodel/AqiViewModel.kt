package com.jcu.cp3406.cp3406assignment1aqi.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jcu.cp3406.cp3406assignment1aqi.data.model.AqiResponse
import com.jcu.cp3406.cp3406assignment1aqi.data.repository.AqiRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AqiViewModel(
    private val repository: AqiRepository
) : ViewModel() {

    val aqiData = MutableLiveData<AqiResponse?>()
    val isLoading = MutableLiveData(false)
    val errorMessage = MutableLiveData("")

    val currentCity = MutableLiveData("Melbourne")
    val showDetailedPollutants = MutableLiveData(true)
    val useChineseStandard = MutableLiveData(false)

    private val cityCoordinates = mapOf(
        "Melbourne" to Pair(-37.81, 144.96),
        "Sydney" to Pair(-33.87, 151.21),
        "Brisbane" to Pair(-27.47, 153.03),
        "Beijing" to Pair(39.90, 116.40)
    )

    fun loadAqiData() {
        isLoading.postValue(true)
        errorMessage.postValue("")

        val city = currentCity.value ?: "Melbourne"
        val (lat, lon) = cityCoordinates[city] ?: cityCoordinates["Melbourne"]!!

        repository.fetchAirQuality(lat, lon).enqueue(object : Callback<AqiResponse> {
            override fun onResponse(call: Call<AqiResponse>, response: Response<AqiResponse>) {
                isLoading.postValue(false)
                if (response.isSuccessful && response.body() != null) {
                    aqiData.postValue(response.body())
                } else {
                    aqiData.postValue(repository.getMockAqiData())
                    errorMessage.postValue("API unavailable, showing demo data")
                }
            }

            override fun onFailure(call: Call<AqiResponse>, t: Throwable) {
                isLoading.postValue(false)
                aqiData.postValue(repository.getMockAqiData())
                errorMessage.postValue("Network error, showing demo data")
            }
        })
    }

    fun switchCity(cityName: String) {
        currentCity.postValue(cityName)
        loadAqiData()
    }
}