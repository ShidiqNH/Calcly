package com.calcy

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.calcy.databinding.ActivityMassBinding

class MassActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMassBinding
    private var isEditingFirstValue: Boolean = true // Track which value is being edited

    // Full names and abbreviated units for mass
    private val fullUnits = arrayOf("Tonne", "Kilogram", "Gram", "Milligram", "Pound", "Ounce")
    private val abbreviatedUnits = arrayOf("t", "Kg", "g", "Mg", "lb", "oz")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMassBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        setupSpinner(binding.spinnerMass1)
        setupSpinner(binding.spinnerMass2)
        setupMassConversion()
        setupButtonListeners()
        setupFocusListeners()
    }

    private fun setupSpinner(spinner: android.widget.Spinner) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, fullUnits)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Update spinner display with abbreviated unit
                updateSpinnerDisplay(spinner, position)
                convertMass()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateSpinnerDisplay(spinner: android.widget.Spinner, position: Int) {
        val selectedTextView = spinner.selectedView as? android.widget.TextView
        selectedTextView?.text = abbreviatedUnits[position]
    }

    private fun setupMassConversion() {
        binding.spinnerMass1.setSelection(0)
        binding.spinnerMass2.setSelection(1)
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
    }

    private fun setupFocusListeners() {
        binding.massValue1.setOnClickListener {
            isEditingFirstValue = true
            highlightSelectedInput()
        }

        binding.massValue2.setOnClickListener {
            isEditingFirstValue = false
            highlightSelectedInput()
        }
    }

    private fun highlightSelectedInput() {
        val selectedColor = ContextCompat.getColor(this, R.color.operation)
        val defaultColor = ContextCompat.getColor(this, R.color.textPrimary)

        if (isEditingFirstValue) {
            binding.massValue1.setTextColor(selectedColor)
            binding.massValue2.setTextColor(defaultColor)
        } else {
            binding.massValue1.setTextColor(defaultColor)
            binding.massValue2.setTextColor(selectedColor)
        }
    }

    private fun appendToInput(value: String) {
        val inputField = if (isEditingFirstValue) binding.massValue1 else binding.massValue2
        var currentText = inputField.text.toString()

        // Prevent multiple decimals
        if (value == "." && currentText.contains(".")) return

        // Avoid leading zeros
        if (currentText == "0" && value != ".") {
            currentText = ""
        }

        currentText += value
        inputField.text = currentText
        convertMass()
    }

    private fun allClear() {
        binding.massValue1.text = "0"
        binding.massValue2.text = ""
    }

    private fun backspaceInput() {
        val inputField = if (isEditingFirstValue) binding.massValue1 else binding.massValue2
        val currentText = inputField.text.toString()
        if (currentText.isNotEmpty()) {
            inputField.text = currentText.dropLast(1)
        }
        if (inputField.text.isEmpty()) {
            inputField.text = "0"
        }
        convertMass()
    }

    private fun convertMass() {
        val input = (if (isEditingFirstValue) binding.massValue1.text else binding.massValue2.text)
            .toString()
            .toDoubleOrNull() ?: return
        val fromUnit = if (isEditingFirstValue) binding.spinnerMass1.selectedItem.toString() else binding.spinnerMass2.selectedItem.toString()
        val toUnit = if (isEditingFirstValue) binding.spinnerMass2.selectedItem.toString() else binding.spinnerMass1.selectedItem.toString()

        val result = when (fromUnit) {
            "Tonne" -> convertFromTonne(input, toUnit)
            "Kilogram" -> convertFromKilogram(input, toUnit)
            "Gram" -> convertFromGram(input, toUnit)
            "Milligram" -> convertFromMilligram(input, toUnit)
            "Pound" -> convertFromPound(input, toUnit)
            "Ounce" -> convertFromOunce(input, toUnit)
            else -> input
        }

        if (isEditingFirstValue) {
            binding.massValue2.text = String.format("%.2f", result)
        } else {
            binding.massValue1.text = String.format("%.2f", result)
        }
    }

    // Conversion methods for mass
    private fun convertFromTonne(value: Double, toUnit: String): Double {
        return when (toUnit) {
            "Tonne" -> value
            "Kilogram" -> value * 1000
            "Gram" -> value * 1_000_000
            "Milligram" -> value * 1_000_000_000
            "Pound" -> value * 2204.62
            "Ounce" -> value * 35_273.96
            else -> value
        }
    }

    private fun convertFromKilogram(value: Double, toUnit: String): Double {
        return when (toUnit) {
            "Tonne" -> value / 1000
            "Kilogram" -> value
            "Gram" -> value * 1000
            "Milligram" -> value * 1_000_000
            "Pound" -> value * 2.20462
            "Ounce" -> value * 35.274
            else -> value
        }
    }

    private fun convertFromGram(value: Double, toUnit: String): Double {
        return when (toUnit) {
            "Tonne" -> value / 1_000_000
            "Kilogram" -> value / 1000
            "Gram" -> value
            "Milligram" -> value * 1000
            "Pound" -> value / 453.592
            "Ounce" -> value / 28.3495
            else -> value
        }
    }

    private fun convertFromMilligram(value: Double, toUnit: String): Double {
        return when (toUnit) {
            "Tonne" -> value / 1_000_000_000
            "Kilogram" -> value / 1_000_000
            "Gram" -> value / 1000
            "Milligram" -> value
            "Pound" -> value / 453_592.37
            "Ounce" -> value / 28_349.523
            else -> value
        }
    }

    private fun convertFromPound(value: Double, toUnit: String): Double {
        return when (toUnit) {
            "Tonne" -> value / 2204.62
            "Kilogram" -> value / 2.20462
            "Gram" -> value * 453.592
            "Milligram" -> value * 453_592.37
            "Pound" -> value
            "Ounce" -> value * 16
            else -> value
        }
    }

    private fun convertFromOunce(value: Double, toUnit: String): Double {
        return when (toUnit) {
            "Tonne" -> value / 35_273.96
            "Kilogram" -> value / 35.274
            "Gram" -> value * 28.3495
            "Milligram" -> value * 28_349.523
            "Pound" -> value / 16
            "Ounce" -> value
            else -> value
        }
    }
}
