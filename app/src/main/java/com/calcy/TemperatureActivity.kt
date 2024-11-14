package com.calcy

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.calcy.databinding.ActivityTemperatureBinding

class TemperatureActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTemperatureBinding
    private var isEditingFirstValue: Boolean = true // Track which value is being edited

    // Full names and abbreviated units
    private val fullUnits = arrayOf("Celsius", "Fahrenheit", "Kelvin", "Rankine", "Réaumur")
    private val abbreviatedUnits = arrayOf("°C", "°F", "K", "°R", "°Re")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTemperatureBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        setupSpinner(binding.spinnerTemperature1)
        setupSpinner(binding.spinnerTemperature2)
        setupTemperatureConversion()
        setupButtonListeners()
        setupFocusListeners()
    }

    private fun setupSpinner(spinner: android.widget.Spinner) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, fullUnits)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateSpinnerDisplay(spinner, position)
                convertTemperature()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateSpinnerDisplay(spinner: android.widget.Spinner, position: Int) {
        val selectedTextView = spinner.selectedView as? android.widget.TextView
        selectedTextView?.text = abbreviatedUnits[position]
    }

    private fun setupTemperatureConversion() {
        binding.spinnerTemperature1.setSelection(0)
        binding.spinnerTemperature2.setSelection(1)
    }

    private fun setupButtonListeners() {
        val numberButtons = listOf(
            binding.buttonZero, binding.buttonOne, binding.buttonTwo,
            binding.buttonThree, binding.buttonFour, binding.buttonFive,
            binding.buttonSix, binding.buttonSeven, binding.buttonEight,
            binding.buttonNine
        )

        for (button in numberButtons) {
            button.setOnClickListener {
                appendToInput((it as Button).text.toString())
            }
        }

        binding.buttonDecimal.setOnClickListener { appendToInput(".") }
        binding.buttonAllClear.setOnClickListener { allClear() }
        binding.backspace.setOnClickListener { backspaceInput() }
        binding.buttonPlusMinus.setOnClickListener { toggleSign() } // Add this line
    }

    private fun toggleSign() {
        val inputField = if (isEditingFirstValue) binding.temperatureValue1 else binding.temperatureValue2
        val currentText = inputField.text.toString()

        // Convert text to a number, toggle its sign, and update the text
        if (currentText.isNotEmpty() && currentText != "0") {
            val currentValue = currentText.toDoubleOrNull()
            if (currentValue != null) {
                val toggledValue = -currentValue
                inputField.text = String.format("%.2f", toggledValue)
                convertTemperature()
            }
        }
    }

    private fun setupFocusListeners() {
        binding.temperatureValue1.setOnClickListener {
            isEditingFirstValue = true
            highlightSelectedInput()
        }

        binding.temperatureValue2.setOnClickListener {
            isEditingFirstValue = false
            highlightSelectedInput()
        }
    }

    private fun highlightSelectedInput() {
        val selectedColor = ContextCompat.getColor(this, R.color.operation)
        val defaultColor = ContextCompat.getColor(this, R.color.textPrimary)

        if (isEditingFirstValue) {
            binding.temperatureValue1.setTextColor(selectedColor)
            binding.temperatureValue2.setTextColor(defaultColor)
        } else {
            binding.temperatureValue1.setTextColor(defaultColor)
            binding.temperatureValue2.setTextColor(selectedColor)
        }
    }

    private fun appendToInput(value: String) {
        val inputField = if (isEditingFirstValue) binding.temperatureValue1 else binding.temperatureValue2
        var currentText = inputField.text.toString()

        // Prevent multiple decimals
        if (value == "." && currentText.contains(".")) return

        // Avoid leading zeros
        if (currentText == "0" && value != ".") {
            currentText = ""
        }

        currentText += value
        inputField.text = currentText
        convertTemperature()
    }

    private fun allClear() {
        binding.temperatureValue1.text = "0"
        binding.temperatureValue2.text = ""
    }

    private fun backspaceInput() {
        val inputField = if (isEditingFirstValue) binding.temperatureValue1 else binding.temperatureValue2
        val currentText = inputField.text.toString()
        if (currentText.isNotEmpty()) {
            inputField.text = currentText.dropLast(1)
        }
        if (inputField.text.isEmpty()) {
            inputField.text = "0"
        }
        convertTemperature()
    }

    private fun convertTemperature() {
        val input = (if (isEditingFirstValue) binding.temperatureValue1.text else binding.temperatureValue2.text)
            .toString()
            .toDoubleOrNull() ?: return
        val fromUnit = if (isEditingFirstValue) binding.spinnerTemperature1.selectedItem.toString() else binding.spinnerTemperature2.selectedItem.toString()
        val toUnit = if (isEditingFirstValue) binding.spinnerTemperature2.selectedItem.toString() else binding.spinnerTemperature1.selectedItem.toString()

        val result = when (fromUnit) {
            "Celsius" -> convertFromCelsius(input, toUnit)
            "Fahrenheit" -> convertFromFahrenheit(input, toUnit)
            "Kelvin" -> convertFromKelvin(input, toUnit)
            "Rankine" -> convertFromRankine(input, toUnit)
            "Réaumur" -> convertFromReaumur(input, toUnit)
            else -> input
        }

        if (isEditingFirstValue) {
            binding.temperatureValue2.text = String.format("%.2f", result)
        } else {
            binding.temperatureValue1.text = String.format("%.2f", result)
        }
    }

    // Conversion methods (same as before)
    private fun convertFromCelsius(value: Double, toUnit: String): Double {
        return when (toUnit) {
            "Celsius" -> value
            "Fahrenheit" -> value * 9 / 5 + 32
            "Kelvin" -> value + 273.15
            "Rankine" -> (value + 273.15) * 9 / 5
            "Réaumur" -> value * 4 / 5
            else -> value
        }
    }

    private fun convertFromFahrenheit(value: Double, toUnit: String): Double {
        return when (toUnit) {
            "Celsius" -> (value - 32) * 5 / 9
            "Fahrenheit" -> value
            "Kelvin" -> (value + 459.67) * 5 / 9
            "Rankine" -> value + 459.67
            "Réaumur" -> (value - 32) * 4 / 9
            else -> value
        }
    }

    private fun convertFromKelvin(value: Double, toUnit: String): Double {
        return when (toUnit) {
            "Celsius" -> value - 273.15
            "Fahrenheit" -> value * 9 / 5 - 459.67
            "Kelvin" -> value
            "Rankine" -> value * 9 / 5
            "Réaumur" -> (value - 273.15) * 4 / 5
            else -> value
        }
    }

    private fun convertFromRankine(value: Double, toUnit: String): Double {
        return when (toUnit) {
            "Celsius" -> (value - 491.67) * 5 / 9
            "Fahrenheit" -> value - 459.67
            "Kelvin" -> value * 5 / 9
            "Rankine" -> value
            "Réaumur" -> (value - 491.67) * 4 / 9
            else -> value
        }
    }

    private fun convertFromReaumur(value: Double, toUnit: String): Double {
        return when (toUnit) {
            "Celsius" -> value * 5 / 4
            "Fahrenheit" -> value * 9 / 4 + 32
            "Kelvin" -> value * 5 / 4 + 273.15
            "Rankine" -> (value * 5 / 4 + 273.15) * 9 / 5
            "Réaumur" -> value
            else -> value
        }
    }
}
