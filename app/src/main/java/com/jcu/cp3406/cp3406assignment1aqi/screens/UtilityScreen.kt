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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jcu.cp3406.cp3406assignment1aqi.presentation.viewmodel.AqiViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class) // 声明使用 M3 的实验性下拉刷新 API
@Composable
fun UtilityScreen(
    modifier: Modifier = Modifier,
    viewModel: AqiViewModel = koinViewModel()
) {
    val aqiData by viewModel.aqiData.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState("")
    val currentCity by viewModel.currentCity.observeAsState("Melbourne")
    val showDetailedPollutants by viewModel.showDetailedPollutants.observeAsState(true)
    val useChineseStandard by viewModel.useChineseStandard.observeAsState(false)
    val lastUpdatedTime by viewModel.lastUpdatedTime.observeAsState("")

    // 初始化下拉刷新状态
    val pullRefreshState = rememberPullToRefreshState()

    // 页面初次加载时请求数据
    LaunchedEffect(Unit) {
        viewModel.loadAqiData()
    }

    // 监听下拉刷新动作触发 API 请求
    if (pullRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            viewModel.loadAqiData()
        }
    }

    // 监听加载状态，当加载完成时，收起刷新动画
    LaunchedEffect(isLoading) {
        if (!isLoading) {
            pullRefreshState.endRefresh()
        }
    }

    // 使用 Box 作为最外层，以支持嵌套滚动和刷新指示器的叠加
    Box(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(pullRefreshState.nestedScrollConnection)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // 关键：开启垂直滚动以支持下拉动作
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
                text = "Current Location: $currentCity",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // 新增：显示最后更新时间
            if (lastUpdatedTime.isNotEmpty()) {
                Text(
                    text = "Last updated: $lastUpdatedTime",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(20.dp))
            }

            // 首次加载的中心 Loading 圈
            if (isLoading && aqiData == null) {
                Box(modifier = Modifier.height(300.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            aqiData?.let { data ->
                val aqiValue = data.current.european_aqi
                val aqiLevel = getAqiLevel(aqiValue, useChineseStandard)
                val aqiColor = getAqiColor(aqiValue)
                val healthAdvice = getHealthAdvice(aqiValue, useChineseStandard)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
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

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "💡",
                            fontSize = 20.sp,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Text(
                            text = healthAdvice,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp
                        )
                    }
                }

                if (showDetailedPollutants) {
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
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = errorMessage,
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
            // 底部留白，确保能滚动到底
            Spacer(modifier = Modifier.height(40.dp))
        }

        // 下拉刷新指示器 (置于页面顶部)
        PullToRefreshContainer(
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
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

fun getHealthAdvice(aqi: Int, useChineseStandard: Boolean): String {
    return if (useChineseStandard) {
        when {
            aqi <= 50 -> "完美的一天！尽情享受户外活动吧。"
            aqi <= 100 -> "空气质量尚可，极少数敏感人群请注意。"
            aqi <= 150 -> "轻度污染。敏感人群应减少户外剧烈运动。"
            aqi <= 200 -> "不健康。出门请佩戴口罩，减少剧烈运动。"
            else -> "严重污染！请尽量留在室内，开启空气净化器。"
        }
    } else {
        when {
            aqi <= 50 -> "A perfect day! Enjoy your outdoor activities."
            aqi <= 100 -> "Air quality is acceptable. Sensitive groups should monitor their health."
            aqi <= 150 -> "Mildly polluted. Sensitive groups should reduce prolonged outdoor exertion."
            aqi <= 200 -> "Unhealthy. Wear a mask outdoors and reduce heavy exertion."
            else -> "Hazardous! Avoid all outdoor physical activities."
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