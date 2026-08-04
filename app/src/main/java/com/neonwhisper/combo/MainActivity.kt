package com.neonwhisper.combo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
                    SettingsScreen()
                }
            }
        }
    }
}
