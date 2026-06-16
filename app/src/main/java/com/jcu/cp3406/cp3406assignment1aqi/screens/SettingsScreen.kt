package com.jcu.cp3406.cp3406assignment1aqi.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jcu.cp3406.cp3406assignment1aqi.presentation.viewmodel.AqiViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: AqiViewModel = koinViewModel() // 使用 Koin 注入同一个 ViewModel 实例
) {
    // 观察 ViewModel 中的状态，默认为 true
    val showDetailedPollutants by viewModel.showDetailedPollutants.observeAsState(initial = true)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Show Pollutant Details",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Display PM2.5, PM10, and O3 cards on the Home screen",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Switch(
                checked = showDetailedPollutants,
                onCheckedChange = { isChecked ->
                    // 触发 ViewModel 中的更新方法
                    viewModel.toggleDetailedPollutants(isChecked)
                }
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
    }
}