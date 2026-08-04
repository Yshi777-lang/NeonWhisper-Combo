package com.neonwhisper.combo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.neonwhisper.combo.ui.settings.SettingsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // В будущем здесь будет MaterialTheme с нашей фиолетовой палитрой
            SettingsScreen()
        }
    }
}
