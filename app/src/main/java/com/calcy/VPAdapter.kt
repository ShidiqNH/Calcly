package com.calcy

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class VPAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 2 // Number of tabs
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CalculatorFragment() // First tab: Calculator
            1 -> MenuFragment()        // Second tab: Menu
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}
