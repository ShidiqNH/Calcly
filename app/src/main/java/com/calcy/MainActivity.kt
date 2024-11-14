package com.calcy

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.calcy.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var vpAdapter: VPAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the adapter for ViewPager
        vpAdapter = VPAdapter(this)
        binding.viewPager.adapter = vpAdapter

        // Connect TabLayout with ViewPager2
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            // Inflate custom tab layout
            val tabView = layoutInflater.inflate(R.layout.tab_layout, null)
            tab.customView = tabView

            // Set icons for each tab
            when (position) {
                0 -> {
                    tabView.findViewById<ImageView>(R.id.tabIcon).setImageResource(R.drawable.calculator) // Replace with your icon
                }
                1 -> {
                    tabView.findViewById<ImageView>(R.id.tabIcon).setImageResource(R.drawable.menu) // Replace with your icon
                }
            }
        }.attach()

        // Change icon colors based on tab selection
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val tabView = tab.customView
                tabView?.let {
                    val iconView = it.findViewById<ImageView>(R.id.tabIcon)
                    // Change icon color when selected
                    iconView.setColorFilter(getColor(R.color.operation), android.graphics.PorterDuff.Mode.SRC_IN)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                val tabView = tab.customView
                tabView?.let {
                    val iconView = it.findViewById<ImageView>(R.id.tabIcon)
                    // Reset icon color when unselected
                    iconView.clearColorFilter() // Or set it to a default color
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                // No action needed here
            }
        })

        // Set initial icon color for the first tab
        binding.tabLayout.getTabAt(0)?.let { firstTab ->
            val firstTabView = firstTab.customView
            firstTabView?.let {
                val iconView = it.findViewById<ImageView>(R.id.tabIcon)
                iconView.setColorFilter(getColor(R.color.operation), android.graphics.PorterDuff.Mode.SRC_IN)
            }
        }
    }

    fun onThermoClick(view: View) {
        val intent = Intent(this, TemperatureActivity::class.java)
        startActivity(intent)
    }
    fun onWeightClick(view: View) {
        val intent = Intent(this, MassActivity::class.java)
        startActivity(intent)

    }
    fun onLengthClick(view: View) {
        val intent = Intent(this, LengthActivity::class.java)
        startActivity(intent)

    }
    fun onCurrencyClick(view: View) {
        val intent = Intent(this, CurrencyActivity::class.java)
        startActivity(intent)

    }
    fun onBmiClick(view: View) {
        val intent = Intent(this, BMIActivity::class.java)
        startActivity(intent)

    }
    fun onTimeClick(view: View) {
        val intent = Intent(this, TimeActivity::class.java)
        startActivity(intent)

    }
}
