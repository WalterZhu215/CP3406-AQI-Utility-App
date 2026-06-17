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

    // 新增：记录最后更新的时间
    val lastUpdatedTime = MutableLiveData("")

    private val cityCoordinates = mapOf(
        "Melbourne" to Pair(-37.81, 144.96),
        "Sydney" to Pair(-33.87, 151.21),
        "Brisbane" to Pair(-27.47, 153.03),
        "Beijing" to Pair(39.90, 116.40),
        "Shanghai" to Pair(31.23, 121.47),
        "Tokyo" to Pair(35.67, 139.65),
        "New York" to Pair(40.71, -74.00),
        "London" to Pair(51.50, -0.12)
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
                updateTimestamp() // 无论成功与否，刷新时间戳

                if (response.isSuccessful && response.body() != null) {
                    aqiData.postValue(response.body())
                } else {
                    aqiData.postValue(repository.getMockAqiData())
                    errorMessage.postValue("API unavailable, showing demo data")
                }
            }

            override fun onFailure(call: Call<AqiResponse>, t: Throwable) {
                isLoading.postValue(false)
                updateTimestamp() // 网络失败也要刷新时间戳（展示demo数据的时间）

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