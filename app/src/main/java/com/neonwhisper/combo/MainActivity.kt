package com.neonwhisper.combo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.neonwhisper.combo.adapters.FragmentPagerAdapter

class MainActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var adapter: FragmentPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager = findViewById(R.id.viewPager)
        bottomNav = findViewById(R.id.bottomNav)
        adapter = FragmentPagerAdapter(this)
        
        viewPager.adapter = adapter
        
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_chat -> {
                    viewPager.currentItem = 0
                    true
                }
                R.id.nav_browser -> {
                    viewPager.currentItem = 1
                    true
                }
                R.id.nav_linux -> {
                    viewPager.currentItem = 2
                    true
                }
                R.id.nav_clipboard -> {
                    viewPager.currentItem = 3
                    true
                }
                R.id.nav_soul -> {
                    viewPager.currentItem = 4
                    true
                }
                else -> false
            }
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val itemId = when(position) {
                    0 -> R.id.nav_chat
                    1 -> R.id.nav_browser
                    2 -> R.id.nav_linux
                    3 -> R.id.nav_clipboard
                    4 -> R.id.nav_soul
                    else -> R.id.nav_chat
                }
                bottomNav.menu.findItem(itemId)?.isChecked = true
            }
        })

        if (savedInstanceState == null) {
            bottomNav.selectedItemId = R.id.nav_chat
        }
    }
}
