package com.neonwhisper.combo.ui.settings

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
    val purpleSurface = Color(0xFF1E1E2E)
    val purpleBackground = Color(0xFF12121C)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("⚙️ Настройки Провайдера", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = purplePrimary
                )
            )
        },
        containerColor = purpleBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Выбор провайдера
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = settings.provider.uppercase(),
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Провайдер", color = Color.LightGray) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = purplePrimary,
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    containerColor = purpleSurface
                ) {
                    DropdownMenuItem(
                        text = { Text("Qwen (Alibaba)", color = Color.White) },
                        onClick = { 
                            viewModel.updateSetting { current ->
                                current.copy(
                                    provider = "qwen", 
                                    modelId = "qwen-max", 
                                    baseUrl = "https://dashscope-intl.aliyuncs.com/api/v1"
                                )
                            }
                            expanded = false 
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("DeepSeek", color = Color.White) },
                        onClick = { 
                            viewModel.updateSetting { current ->
                                current.copy(
                                    provider = "deepseek", 
                                    modelId = "deepseek-chat", 
                                    baseUrl = "https://api.deepseek.com/v1"
                                )
                            }
                            expanded = false 
                        }
                    )
                }
            }

            // API Key
            OutlinedTextField(
                value = settings.apiKey,
                onValueChange = { newValue -> 
                    viewModel.updateSetting { current -> current.copy(apiKey = newValue) }
                },
                label = { Text("API Key", color = Color.LightGray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = purplePrimary,
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // Model ID
            OutlinedTextField(
                value = settings.modelId,
                onValueChange = { newValue -> 
                    viewModel.updateSetting { current -> current.copy(modelId = newValue) }
                },
                label = { Text("ID Модели (напр. qwen-max, deepseek-chat)", color = Color.LightGray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = purplePrimary,
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // Размер кэша
            OutlinedTextField(
                value = settings.maxContextMessages.toString(),
                onValueChange = { newValue -> 
                    val intValue = newValue.toIntOrNull() ?: 40
                    viewModel.updateSetting { current -> current.copy(maxContextMessages = intValue) }
                },
                label = { Text("Глубина кэша (последних сообщений)", color = Color.LightGray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = purplePrimary,
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Кнопка сохранения
            Button(
                onClick = { viewModel.saveSettings() },
                enabled = !isSaving && settings.apiKey.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = purplePrimary),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("💾 Сохранить и Активировать", color = Color.White, style = MaterialTheme.typography.titleMedium)
                }
            }
            
            Text(
                text = "💡 Совет: Кэш автоматически обрежет старые сообщения, экономя токены и предотвращая вылеты.",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
