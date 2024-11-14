package com.calcy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.calcy.databinding.ActivityBmiactivityBinding

class BMIActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBmiactivityBinding
    private var isMaleSelected = true // Default to male selection
    private var height = 183 // Default height in cm
    private var weight = 74 // Default weight in kg
    private var age = 19 // Default age

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBmiactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Gender Selection
        binding.maleOption.setOnClickListener {
            selectGender(true)
        }

        binding.femaleOption.setOnClickListener {
            selectGender(false)
        }

        // SeekBar for height
        binding.heightSeekBar.setOnSeekBarChangeListener(object :
            android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                height = progress
                binding.heightValue.text = "$height cm"
            }

            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
        })

        // Weight buttons
        binding.weightMinus.setOnClickListener {
            if (weight > 1) {
                weight--
                binding.weightValue.text = weight.toString()
            }
        }

        binding.weightPlus.setOnClickListener {
            weight++
            binding.weightValue.text = weight.toString()
        }

        // Age buttons
        binding.ageMinus.setOnClickListener {
            if (age > 1) {
                age--
                binding.ageValue.text = age.toString()
            }
        }

        binding.agePlus.setOnClickListener {
            age++
            binding.ageValue.text = age.toString()
        }

        // Calculate BMI button
        binding.calculateButton.setOnClickListener {
            val bmi = calculateBMI(height, weight)
            showBMIPopup(bmi)
        }
    }

    private fun selectGender(isMale: Boolean) {
        isMaleSelected = isMale
        if (isMale) {
            binding.maleIcon.setColorFilter(getColor(R.color.operation))
            binding.maleText.setTextColor(getColor(R.color.operation))
            binding.femaleIcon.setColorFilter(getColor(R.color.textPrimary))
            binding.femaleText.setTextColor(getColor(R.color.textPrimary))
        } else {
            binding.femaleIcon.setColorFilter(getColor(R.color.operation))
            binding.femaleText.setTextColor(getColor(R.color.operation))
            binding.maleIcon.setColorFilter(getColor(R.color.textPrimary))
            binding.maleText.setTextColor(getColor(R.color.textPrimary))
        }
    }

    private fun calculateBMI(height: Int, weight: Int): Double {
        val heightInMeters = height / 100.0
        return weight / (heightInMeters * heightInMeters)
    }

    private fun showBMIPopup(bmi: Double) {
        // Inflate custom layout for the popup
        val inflater = LayoutInflater.from(this)
        val popupView = inflater.inflate(R.layout.bmi_popup, null)

        // Find and set the TextViews and Button in the popup
        val bmiValueTextView = popupView.findViewById<TextView>(R.id.bmiValue)
        val bmiDescriptionTextView = popupView.findViewById<TextView>(R.id.bmiDescription)
        val bmiStatusTextView = popupView.findViewById<TextView>(R.id.bmiStatus)
        val okayButton = popupView.findViewById<Button>(R.id.okayButton)

        // Set values
        bmiValueTextView.text = String.format("%.1f", bmi)
        bmiDescriptionTextView.text = "kg/m²"
        bmiStatusTextView.text = getBMIStatus(bmi)
        bmiStatusTextView.setTextColor(getColor(getBMIStatusColor(bmi)))

        // Create and show the AlertDialog
        val dialog = AlertDialog.Builder(this)
            .setView(popupView)
            .create()

        // Handle Okay button click
        okayButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


    private fun getBMIStatus(bmi: Double): String {
        return when {
            bmi < 18.5 -> "Underweight"
            bmi < 24.9 -> "Normal"
            bmi < 29.9 -> "Overweight"
            else -> "Obese"
        }
    }

    private fun getBMIStatusColor(bmi: Double): Int {
        return when {
            bmi < 18.5 -> R.color.bmi_underweight
            bmi < 24.9 -> R.color.bmi_normal
            bmi < 29.9 -> R.color.bmi_overweight
            else -> R.color.bmi_obese
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
