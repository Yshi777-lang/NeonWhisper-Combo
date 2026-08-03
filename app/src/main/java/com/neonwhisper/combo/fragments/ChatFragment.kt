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
import com.neonwhisper.combo.dialogs.ApiAuthDialog
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
    private lateinit var authDialog: ApiAuthDialog
    
    private var currentApiKey = ""
    private var currentModel = "qwen-turbo"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        repository = ChatRepository(requireContext())
        apiClient = LlmApiClient()
        authDialog = ApiAuthDialog(requireContext(), repository)
        
        rvMessages = view.findViewById(R.id.rvMessages)
        etInput = view.findViewById(R.id.etInput)
        btnSend = view.findViewById(R.id.btnSend)
        spinnerProvider = view.findViewById(R.id.spinnerProvider)
        btnSettings = view.findViewById(R.id.btnSettings)

        messageAdapter = MessageAdapter(messages)
        rvMessages.layoutManager = LinearLayoutManager(requireContext())
        rvMessages.adapter = messageAdapter

        val providers = arrayOf("Qwen (Alibaba)", "OpenAI", "Gemini", "Anthropic")
        spinnerProvider.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, providers)

        btnSettings.setOnClickListener {
            val provider = spinnerProvider.selectedItem.toString()
            authDialog.show(provider, currentApiKey, currentModel) { newKey, newModel ->
                currentApiKey = newKey
                currentModel = newModel
                apiClient.setApiKey(currentApiKey, "", "", "") // Упрощённо
                Toast.makeText(requireContext(), "Saved: $newModel", Toast.LENGTH_SHORT).show()
            }
        }

        btnSend.setOnClickListener { sendMessage() }
        
        loadMessages()
    }

    private fun loadMessages() {
        lifecycleScope.launch {
            // Загрузка из Room базы
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
                val soulPrompt = SoulFragment.getSoul(requireContext())
                val response = apiClient.sendMessageWithModel(selectedProvider, currentModel, text, soulPrompt)
                val aiMessage = Message(response, false, System.currentTimeMillis())
                messages.add(aiMessage)
                messageAdapter.notifyDataSetChanged()
                rvMessages.scrollToPosition(messages.lastIndex)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
