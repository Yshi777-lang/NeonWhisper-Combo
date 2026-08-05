package com.neonwhisper.combo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
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
    val chatViewModel: ChatViewModel = viewModel()
    val browserViewModel: BrowserViewModel = viewModel()
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Верхняя панель
        TopAppBar(
            title = { 
                Text(
                    when (selectedTab) {
                        0 -> "💬 Чат"
                        1 -> "🌐 Браузер"
                        2 -> "⚙️ Настройки"
                        else -> "NeonWhisper Combo"
                    },
                    color = Color.White
                )
            },
            backgroundColor = Color(0xFF9C27B0),
            elevation = 8.dp
        )
        
        // Контент
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                0 -> ChatScreen(viewModel = chatViewModel)
                1 -> BrowserScreen(viewModel = browserViewModel)
                2 -> SettingsScreen()
            }
        }
        
        // Нижняя навигация
        BottomNavigation(backgroundColor = Color(0xFF1E1E2E)) {
            BottomNavigationItem(
                icon = { Icon(Icons.Filled.Chat, contentDescription = "Chat", tint = Color.White) },
                label = { Text("Чат", color = Color.White) },
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                selectedContentColor = Color(0xFF9C27B0),
                unselectedContentColor = Color.Gray
            )
            BottomNavigationItem(
                icon = { Icon(Icons.Filled.Public, contentDescription = "Browser", tint = Color.White) },
                label = { Text("Браузер", color = Color.White) },
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                selectedContentColor = Color(0xFF9C27B0),
                unselectedContentColor = Color.Gray
            )
            BottomNavigationItem(
                icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = Color.White) },
                label = { Text("Настройки", color = Color.White) },
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                selectedContentColor = Color(0xFF9C27B0),
                unselectedContentColor = Color.Gray
            )
        }
    }
}
