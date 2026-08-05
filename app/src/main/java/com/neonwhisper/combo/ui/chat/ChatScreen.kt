package com.neonwhisper.combo.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChatScreen(viewModel: ChatViewModel = viewModel()) {
    val messages by viewModel.messages.collectAsState()
    val isSending by viewModel.isSending.collectAsState()
    val error by viewModel.error.collectAsState()
    val sessionId by viewModel.currentSessionId.collectAsState()
    
    val purplePrimary = Color(0xFF9C27B0)
    val purpleBackground = Color(0xFF12121C)
    val userBubble = Color(0xFF2E2E4E)
    val assistantBubble = Color(0xFF1E1E3E)
    
    val listState = rememberLazyListState()
    var messageInput by remember { mutableStateOf("") }

    // Автопрокрутка к последнему сообщению
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Создаём сессию если нет
    LaunchedEffect(sessionId) {
        if (sessionId == null) {
            viewModel.createSession("My Chat", "Ты мой личный AI помощник. Отвечай подробно и дружелюбно.")
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(purpleBackground)) {
        // Список сообщений
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (messages.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "💬 Начни общение!\nНапиши первое сообщение...",
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.body1
                        )
                    }
                }
            }
            
            items(messages, key = { it.id }) { message ->
                MessageBubble(
                    message = message,
                    isUser = message.role == "user",
                    userColor = userBubble,
                    assistantColor = assistantBubble
                )
            }
            
            // Индикатор отправки
            if (isSending) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Surface(
                            color = assistantBubble,
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    color = purplePrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Печатает...",
                                    color = Color.LightGray,
                                    style = MaterialTheme.typography.body2
                                )
                            }
                        }
                    }
                }
            }
        }

        // Поле ввода
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E1E2E))
                .padding(8.dp)
        ) {
            // Ошибка
            error?.let { errorMsg ->
                Text(
                    text = errorMsg,
                    color = Color.Red,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                OutlinedTextField(
                    value = messageInput,
                    onValueChange = { messageInput = it },
                    placeholder = { Text("Введите сообщение...", color = Color.Gray) },
                    modifier = Modifier.weight(1f).heightIn(max = 120.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = purplePrimary,
                        unfocusedBorderColor = Color.Gray,
                        textColor = Color.White,
                        backgroundColor = Color.Transparent
                    ),
                    maxLines = 4
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                IconButton(
                    onClick = {
                        if (messageInput.isNotBlank() && !isSending) {
                            viewModel.sendMessage(messageInput)
                            messageInput = ""
                        }
                    },
                    enabled = messageInput.isNotBlank() && !isSending,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Send,
                        contentDescription = "Send",
                        tint = if (messageInput.isNotBlank() && !isSending) purplePrimary else Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: ChatMessage,
    isUser: Boolean,
    userColor: Color,
    assistantColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = if (isUser) userColor else assistantColor,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .widthIn(max = 280.dp)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.content,
                    color = Color.White,
                    style = MaterialTheme.typography.body1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = android.text.format.DateFormat.format("HH:mm", message.timestamp).toString(),
                    color = Color.Gray,
                    style = MaterialTheme.typography.caption
                )
            }
        }
    }
}
