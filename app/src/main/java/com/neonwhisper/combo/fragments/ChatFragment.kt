package com.neonwhisper.combo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.neonwhisper.combo.R
import com.neonwhisper.combo.data.Message
import com.neonwhisper.combo.data.ChatRepository
import com.neonwhisper.combo.adapters.MessageAdapter
import com.neonwhisper.combo.api.LlmApiClient
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ChatFragment : Fragment() {
    private lateinit var rvMessages: RecyclerView
    private lateinit var etInput: EditText
    private lateinit var btnSend: Button
    private lateinit var spinnerProvider: Spinner
    private lateinit var btnSettings: ImageButton
    private lateinit var messageAdapter: MessageAdapter
    private val messages = mutableListOf<Message>()
    private lateinit var repository: ChatRepository
    private lateinit var apiClient: LlmApiClient

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        repository = ChatRepository(requireContext())
        apiClient = LlmApiClient()
        
        rvMessages = view.findViewById(R.id.rvMessages)
        etInput = view.findViewById(R.id.etInput)
        btnSend = view.findViewById(R.id.btnSend)
        spinnerProvider = view.findViewById(R.id.spinnerProvider)
        btnSettings = view.findViewById(R.id.btnSettings)

        // Настройка RecyclerView
        messageAdapter = MessageAdapter(messages)
        rvMessages.layoutManager = LinearLayoutManager(requireContext())
        rvMessages.adapter = messageAdapter

        // Провайдеры
        val providers = arrayOf("Qwen (Alibaba)", "OpenAI", "Gemini", "Anthropic")
        spinnerProvider.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, providers)

        // Загрузка API ключей
        loadApiKeys()

        btnSettings.setOnClickListener {
            // Открываем SettingsActivity
            Toast.makeText(requireContext(), "Settings coming soon", Toast.LENGTH_SHORT).show()
        }

        btnSend.setOnClickListener {
            sendMessage()
        }
    }

    private fun loadApiKeys() {
        lifecycleScope.launch {
            repository.loadApiKeys()
        }
    }

    private fun sendMessage() {
        val text = etInput.text.toString().trim()
        if (text.isEmpty()) return

        val userMessage = Message(text, true, System.currentTimeMillis())
        messages.add(userMessage)
        messageAdapter.notifyDataSetChanged()
        etInput.text.clear()

        val selectedProvider = spinnerProvider.selectedItem.toString()
        
        lifecycleScope.launch {
            try {
                val response = apiClient.sendMessage(selectedProvider, text, getSoulPrompt())
                val aiMessage = Message(response, false, System.currentTimeMillis())
                messages.add(aiMessage)
                messageAdapter.notifyDataSetChanged()
                rvMessages.scrollToPosition(messages.lastIndex)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun getSoulPrompt(): String {
        return SoulFragment.getSoul(requireContext())
    }
}
