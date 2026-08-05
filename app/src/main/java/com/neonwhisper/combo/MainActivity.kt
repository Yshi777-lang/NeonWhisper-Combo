package com.neonwhisper.combo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Public
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.neonwhisper.combo.ui.browser.BrowserScreen
import com.neonwhisper.combo.ui.settings.SettingsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colors = lightColors(
                    primary = Color(0xFF9C27B0),
                    secondary = Color(0xFFBA68C8)
                )
            ) {
                Surface(modifier = Modifier.fillMaxSize().background(Color(0xFF12121C))) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Контент
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                0 -> BrowserScreen()
                1 -> SettingsScreen()
            }
        }
        
        // Нижняя навигация
        BottomNavigation(backgroundColor = Color(0xFF1E1E2E)) {
            BottomNavigationItem(
                icon = { Icon(Icons.Filled.Public, contentDescription = "Browser", tint = Color.White) },
                label = { Text("Браузер", color = Color.White) },
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                selectedContentColor = Color(0xFF9C27B0),
                unselectedContentColor = Color.Gray
            )
            BottomNavigationItem(
                icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = Color.White) },
                label = { Text("Настройки", color = Color.White) },
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                selectedContentColor = Color(0xFF9C27B0),
                unselectedContentColor = Color.Gray
            )
        }
    }
}
