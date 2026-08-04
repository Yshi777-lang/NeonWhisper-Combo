package com.neonwhisper.combo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val textView = TextView(this).apply {
            text = "NeonWhisper Combo v1.2\n\nCore initialized!\nRoom DB + API Client ready."
            textSize = 18f
            setPadding(50, 100, 50, 100)
        }
        setContentView(textView)
    }
}
