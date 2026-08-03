package com.neonwhisper.combo.dialogs

import android.app.AlertDialog
import android.content.Context
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.ArrayAdapter
import com.neonwhisper.combo.data.ChatRepository

class ApiAuthDialog(private val context: Context, private val repository: ChatRepository) {
    
    private val qwenModels = arrayOf("qwen-turbo", "qwen-plus", "qwen-max", "qwen-long-context")
    private val openaiModels = arrayOf("gpt-3.5-turbo", "gpt-4", "gpt-4-turbo", "gpt-4o")
    private val geminiModels = arrayOf("gemini-1.5-flash", "gemini-1.5-pro", "gemini-pro")
    private val anthropicModels = arrayOf("claude-3-haiku-20240307", "claude-3-sonnet-20240229", "claude-3-opus-20240229")

    fun show(provider: String, currentKey: String, currentModel: String, callback: (String, String) -> Unit) {
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val etApiKey = EditText(context).apply {
            hint = "API Key"
            setText(currentKey)
        }

        val modelSpinner = Spinner(context)
        val models = when(provider) {
            "Qwen (Alibaba)" -> qwenModels
            "OpenAI" -> openaiModels
            "Gemini" -> geminiModels
            "Anthropic" -> anthropicModels
            else -> emptyArray()
        }
        
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, models)
        modelSpinner.adapter = adapter
        val currentModelIndex = models.indexOf(currentModel)
        if (currentModelIndex >= 0) modelSpinner.setSelection(currentModelIndex)

        layout.addView(etApiKey)
        layout.addView(modelSpinner)

        AlertDialog.Builder(context)
            .setTitle("$provider - Settings")
            .setView(layout)
            .setPositiveButton("Save") { dialog, _ ->
                val newKey = etApiKey.text.toString()
                val selectedModel = models[modelSpinner.selectedItemPosition]
                callback(newKey, selectedModel)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
