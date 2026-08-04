package com.neonwhisper.combo.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {
    val settings by viewModel.settings.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val purplePrimary = Color(0xFF9C27B0)
    val purpleBackground = Color(0xFF12121C)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("⚙️ Настройки", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = purplePrimary
                )
            )
        },
        modifier = Modifier.background(purpleBackground)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Провайдер: ${settings.provider}", color = Color.White)
            
            OutlinedTextField(
                value = settings.apiKey,
                onValueChange = { newValue -> 
                    viewModel.updateSetting { current -> current.copy(apiKey = newValue) }
                },
                label = { Text("API Key", color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = purplePrimary,
                    unfocusedBorderColor = Color.Gray
                )
            )
            
            OutlinedTextField(
                value = settings.modelId,
                onValueChange = { newValue -> 
                    viewModel.updateSetting { current -> current.copy(modelId = newValue) }
                },
                label = { Text("Model ID", color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = purplePrimary,
                    unfocusedBorderColor = Color.Gray
                )
            )
            
            Button(
                onClick = { viewModel.saveSettings() },
                enabled = !isSaving && settings.apiKey.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = purplePrimary),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("💾 Сохранить", color = Color.White)
                }
            }
            
            Text(
                text = "💡 Введи API ключ Qwen или DeepSeek",
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
