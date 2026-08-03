package com.neonwhisper.combo.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import com.neonwhisper.combo.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

val Context.dataStore by preferencesDataStore(name = "soul_prefs")

class SoulFragment : Fragment() {
    private lateinit var etSoul: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_soul, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        etSoul = view.findViewById(R.id.etSoul)

        // Загружаем сохранённую душу
        CoroutineScope(Dispatchers.IO).launch {
            val soul = requireContext().dataStore.data.first()[stringPreferencesKey("soul_prompt")] ?: ""
            requireActivity().runOnUiThread {
                etSoul.setText(soul)
            }
        }

        view.findViewById<Button>(R.id.btnSaveSoul).setOnClickListener {
            val soul = etSoul.text.toString()
            CoroutineScope(Dispatchers.IO).launch {
                requireContext().dataStore.edit { prefs ->
                    prefs[stringPreferencesKey("soul_prompt")] = soul
                }
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Soul saved!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        suspend fun getSoul(context: Context): String {
            return context.dataStore.data.first()[stringPreferencesKey("soul_prompt")] ?: ""
        }
    }
}
