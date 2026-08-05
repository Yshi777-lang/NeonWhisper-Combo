package com.neonwhisper.combo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neonwhisper.combo.ui.browser.BrowserScreen
import com.neonwhisper.combo.ui.browser.BrowserViewModel
import com.neonwhisper.combo.ui.chat.ChatScreen
import com.neonwhisper.combo.ui.chat.ChatViewModel
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
                Surface(
                    modifier = Modifier.fillMaxSize().background(Color(0xFF12121C)),
                    color = Color(0xFF12121C)
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val chatViewModel: ChatViewModel = viewModel()
    val browserViewModel: BrowserViewModel = viewModel()
    
    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTab,
            backgroundColor = Color(0xFF9C27B0),
            contentColor = Color.White
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Чат", color = if (selectedTab == 0) Color.White else Color.LightGray) },
                icon = { Icon(Icons.Filled.Chat, contentDescription = "Chat", tint = if (selectedTab == 0) Color.White else Color.LightGray) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Браузер", color = if (selectedTab == 1) Color.White else Color.LightGray) },
                icon = { Icon(Icons.Filled.Public, contentDescription = "Browser", tint = if (selectedTab == 1) Color.White else Color.LightGray) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Настройки", color = if (selectedTab == 2) Color.White else Color.LightGray) },
                icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = if (selectedTab == 2) Color.White else Color.LightGray) }
            )
        }
        
        when (selectedTab) {
            0 -> ChatScreen(viewModel = chatViewModel)
            1 -> BrowserScreen(viewModel = browserViewModel)
            2 -> SettingsScreen()
        }
    }
}
