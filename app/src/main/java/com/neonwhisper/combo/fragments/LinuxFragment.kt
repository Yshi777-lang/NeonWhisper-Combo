package com.neonwhisper.combo.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.neonwhisper.combo.R

class LinuxFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_linux, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btnOpenTermux).setOnClickListener {
            try {
                val intent = requireActivity().packageManager.getLaunchIntentForPackage("com.termux")
                if (intent != null) startActivity(intent)
                else Toast.makeText(requireContext(), "Termux not installed", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
            }
        }

        view.findViewById<Button>(R.id.btnSendCommand).setOnClickListener {
            Toast.makeText(requireContext(), "Coming soon: Termux API integration", Toast.LENGTH_SHORT).show()
        }
    }
}
