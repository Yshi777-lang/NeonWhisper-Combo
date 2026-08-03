package com.neonwhisper.combo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.neonwhisper.combo.fragments.*

class MainActivity : AppCompatActivity() {
    private val chatFragment = ChatFragment()
    private val browserFragment = BrowserFragment()
    private val linuxFragment = LinuxFragment()
    private val clipboardFragment = ClipboardFragment()
    private val soulFragment = SoulFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_chat -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, chatFragment)
                        .commit()
                    true
                }
                R.id.nav_browser -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, browserFragment)
                        .commit()
                    true
                }
                R.id.nav_linux -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, linuxFragment)
                        .commit()
                    true
                }
                R.id.nav_clipboard -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, clipboardFragment)
                        .commit()
                    true
                }
                R.id.nav_soul -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, soulFragment)
                        .commit()
                    true
                }
                else -> false
            }
        }

        if (savedInstanceState == null) {
            bottomNav.selectedItemId = R.id.nav_chat
        }
    }
}
