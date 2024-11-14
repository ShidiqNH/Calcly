package com.calcy

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.calcy.databinding.ActivityBmiactivityBinding
import com.calcy.databinding.ActivityMainBinding

class BMIActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBmiactivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBmiactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the toolbar as the action bar
        setSupportActionBar(binding.toolbar)

        // Disable default title
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Enable the back button on the action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Set up custom title (if required)
        binding.toolbar.title = ""

        // Handle back press using OnBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Custom logic for handling back navigation
                finish() // Close the activity or implement custom behavior here
            }
        })

        // Handle navigation icon (back button) click
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        // Handle the back button in the action bar
        onBackPressedDispatcher.onBackPressed()
        return true
    }

}