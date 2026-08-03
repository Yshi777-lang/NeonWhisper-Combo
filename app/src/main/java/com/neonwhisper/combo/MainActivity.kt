package com.neonwhisper.combo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnBrowser).setOnClickListener {
            startActivity(Intent(this, BrowserActivity::class.java))
        }

        findViewById<Button>(R.id.btnLinux).setOnClickListener {
            openTermux()
        }

        findViewById<Button>(R.id.btnVPN).setOnClickListener {
            openVPN()
        }
    }

    private fun openTermux() {
        try {
            val intent = packageManager.getLaunchIntentForPackage("com.termux")
            if (intent != null) startActivity(intent)
            else Toast.makeText(this, "Termux не установлен", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openVPN() {
        try {
            val intent = packageManager.getLaunchIntentForPackage("com.v2ray.ang")
            if (intent != null) startActivity(intent)
            else Toast.makeText(this, "v2rayNG не установлен", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show()
        }
    }
}
