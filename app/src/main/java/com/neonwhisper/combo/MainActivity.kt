package com.neonwhisper.combo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen() {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()
    val chatViewModel: ChatViewModel = viewModel()
    val browserViewModel: BrowserViewModel = viewModel()
    
    val titles = listOf("💬 Чат", " Браузер", "⚙️ Настройки")
    
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(text = titles[pagerState.currentPage], color = Color.White) },
            backgroundColor = Color(0xFF9C27B0),
            elevation = 8.dp
        )
        
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> ChatScreen(viewModel = chatViewModel)
                1 -> BrowserScreen(viewModel = browserViewModel)
                2 -> SettingsScreen()
            }
        }
        
        BottomNavigation(backgroundColor = Color(0xFF1E1E2E)) {
            BottomNavigationItem(
                icon = { Icon(Icons.Filled.Chat, contentDescription = "Chat", tint = Color.White) },
                label = { Text("Чат", color = if (pagerState.currentPage == 0) Color(0xFF9C27B0) else Color.Gray) },
                selected = pagerState.currentPage == 0,
                onClick = { scope.launch { pagerState.animateScrollToPage(0) } },
                selectedContentColor = Color(0xFF9C27B0),
                unselectedContentColor = Color.Gray
            )
            BottomNavigationItem(
                icon = { Icon(Icons.Filled.Public, contentDescription = "Browser", tint = Color.White) },
                label = { Text("Браузер", color = if (pagerState.currentPage == 1) Color(0xFF9C27B0) else Color.Gray) },
                selected = pagerState.currentPage == 1,
                onClick = { scope.launch { pagerState.animateScrollToPage(1) } },
                selectedContentColor = Color(0xFF9C27B0),
                unselectedContentColor = Color.Gray
            )
            BottomNavigationItem(
                icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = Color.White) },
                label = { Text("Настройки", color = if (pagerState.currentPage == 2) Color(0xFF9C27B0) else Color.Gray) },
                selected = pagerState.currentPage == 2,
                onClick = { scope.launch { pagerState.animateScrollToPage(2) } },
                selectedContentColor = Color(0xFF9C27B0),
                unselectedContentColor = Color.Gray
            )
        }
    }
}
