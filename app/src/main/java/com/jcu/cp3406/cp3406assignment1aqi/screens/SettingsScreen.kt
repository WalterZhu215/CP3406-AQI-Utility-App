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

/**
 * The Settings Screen Composable.
 * Allows users to customize their App experience by toggling specific configurations.
 * Changes made here are immediately reflected in the UtilityScreen via the shared ViewModel.
 */
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: AqiViewModel = koinViewModel()
) {
    // Observe LiveData states from ViewModel to keep UI switches updated
    val showDetailedPollutants by viewModel.showDetailedPollutants.observeAsState(initial = true)
    val useChineseStandard by viewModel.useChineseStandard.observeAsState(initial = false)

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

        // Setting Item 1: Toggle detailed pollutants display
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
                onCheckedChange = { viewModel.toggleDetailedPollutants(it) }
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        // Setting Item 2: Toggle Chinese AQI Calculation Standard
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Use Chinese AQI Standard",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Evaluate air quality categories and levels using China National Standards (HJ 633-2012)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Switch(
                checked = useChineseStandard,
                onCheckedChange = { viewModel.toggleChineseStandard(it) }
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
    }
}