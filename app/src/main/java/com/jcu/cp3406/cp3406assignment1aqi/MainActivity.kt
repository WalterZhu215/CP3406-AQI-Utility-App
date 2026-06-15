package com.jcu.cp3406.cp3406assignment1aqi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.jcu.cp3406.cp3406assignment1aqi.screens.SettingsScreen
import com.jcu.cp3406.cp3406assignment1aqi.screens.UtilityScreen
import com.jcu.cp3406.cp3406assignment1aqi.ui.theme.CP3406Assignment1AQITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CP3406Assignment1AQITheme {
                UtilityApp()
            }
        }
    }
}

/**
 * Root composable for the entire app, handles bottom navigation
 */
@Composable
fun UtilityApp() {
    // Track selected navigation tab
    var selectedTab by remember { mutableIntStateOf(0) }
    val navItems = listOf(
        "Home" to Icons.Filled.Home,
        "Settings" to Icons.Filled.Settings
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(imageVector = item.second, contentDescription = item.first) },
                        label = { Text(text = item.first) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index }
                    )
                }
            }
        }
    ) { innerPadding ->
        // Switch screens based on selected tab
        when (selectedTab) {
            0 -> UtilityScreen(modifier = Modifier.padding(innerPadding))
            1 -> SettingsScreen(modifier = Modifier.padding(innerPadding))
        }
    }
}