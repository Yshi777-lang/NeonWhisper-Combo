package com.neonwhisper.combo.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.neonwhisper.combo.fragments.*

class FragmentPagerAdapter(fragmentActivity: androidx.fragment.app.FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    
    private val fragments = listOf(
        ChatFragment(),
        BrowserFragment(),
        LinuxFragment(),
        ClipboardFragment(),
        SoulFragment()
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}
