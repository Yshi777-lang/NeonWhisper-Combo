package com.neonwhisper.combo.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.neonwhisper.combo.R

class ClipboardFragment : Fragment() {
    private lateinit var etCurrent: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_clipboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        etCurrent = view.findViewById(R.id.etCurrent)

        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        view.findViewById<Button>(R.id.btnPaste).setOnClickListener {
            val clip = clipboard.primaryClip
            if (clip != null && clip.itemCount > 0) {
                etCurrent.setText(clip.getItemAt(0).text)
            } else {
                Toast.makeText(requireContext(), "Clipboard is empty", Toast.LENGTH_SHORT).show()
            }
        }

        view.findViewById<Button>(R.id.btnCopy).setOnClickListener {
            val text = etCurrent.text.toString()
            if (text.isNotEmpty()) {
                val clip = ClipData.newPlainText("NeonWhisper", text)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(requireContext(), "Copied!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
