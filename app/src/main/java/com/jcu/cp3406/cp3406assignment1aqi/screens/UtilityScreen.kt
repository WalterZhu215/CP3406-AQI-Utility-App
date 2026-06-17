package com.jcu.cp3406.cp3406assignment1aqi.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jcu.cp3406.cp3406assignment1aqi.presentation.viewmodel.AqiViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun UtilityScreen(
    modifier: Modifier = Modifier,
    viewModel: AqiViewModel = koinViewModel()
) {
    val aqiData by viewModel.aqiData.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState()
    val errorMessage by viewModel.errorMessage.observeAsState()
    val currentCity by viewModel.currentCity.observeAsState()
    val showDetailedPollutants by viewModel.showDetailedPollutants.observeAsState()
    val useChineseStandard by viewModel.useChineseStandard.observeAsState(initial = false)

    LaunchedEffect(Unit) {
        viewModel.loadAqiData()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Air Quality Monitor",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Current Location: ${currentCity ?: ""}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        if (isLoading == true) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            return@Column
        }

        aqiData?.let { data ->
            val aqiValue = data.current.european_aqi
            val aqiLevel = getAqiLevel(aqiValue, useChineseStandard)
            val aqiColor = getAqiColor(aqiValue)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = aqiColor.copy(alpha = 0.15f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (useChineseStandard) "空气质量指数 (AQI)" else "AQI Index",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = aqiValue.toString(),
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Bold,
                        color = aqiColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = aqiLevel,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = aqiColor
                    )
                }
            }

            if (showDetailedPollutants == true) {
                Text(
                    text = "Pollutant Details",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    PollutantCard(label = "PM2.5", value = "${data.current.pm2_5}", unit = "μg/m³")
                    PollutantCard(label = "PM10", value = "${data.current.pm10}", unit = "μg/m³")
                    PollutantCard(label = "O₃", value = "${data.current.o3}", unit = "μg/m³")
                }
            }
        }

        if (errorMessage.orEmpty().isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = errorMessage.orEmpty(),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Switch City",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            viewModel.availableCities.forEach { city ->
                val isSelected = (currentCity == city)
                if (isSelected) {
                    Button(onClick = { /* Already selected */ }) {
                        Text(city)
                    }
                } else {
                    OutlinedButton(onClick = { viewModel.switchCity(city) }) {
                        Text(city)
                    }
                }
            }
        }
    }
}

@Composable
fun PollutantCard(label: String, value: String, unit: String) {
    Card(
        modifier = Modifier.size(100.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = unit,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


fun getAqiLevel(aqi: Int, useChineseStandard: Boolean): String {
    return if (useChineseStandard) {
        when {
            aqi <= 50 -> "优 (Excellent)"
            aqi <= 100 -> "良 (Good)"
            aqi <= 150 -> "轻度污染 (Mildly Polluted)"
            aqi <= 200 -> "中度污染 (Moderately Polluted)"
            else -> "重度污染 (Severely Polluted)"
        }
    } else {
        when {
            aqi <= 50 -> "Good"
            aqi <= 100 -> "Fair"
            aqi <= 150 -> "Moderate"
            aqi <= 200 -> "Poor"
            else -> "Very Poor"
        }
    }
}

fun getAqiColor(aqi: Int): Color {
    return when {
        aqi <= 50 -> Color(0xFF4CAF50)
        aqi <= 100 -> Color(0xFFFFC107)
        aqi <= 150 -> Color(0xFFFF9800)
        aqi <= 200 -> Color(0xFFF44336)
        else -> Color(0xFF9C27B0)
    }
}