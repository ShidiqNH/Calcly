package com.calcy

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.calcy.databinding.ActivityLengthBinding

class LengthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLengthBinding
    private var isEditingFirstValue: Boolean = true // Track which value is being edited

    // Full names and abbreviated units for length
    private val fullUnits = arrayOf("Kilometer", "Meter", "Centimeter", "Millimeter", "Mile", "Yard", "Foot", "Inch")
    private val abbreviatedUnits = arrayOf("km", "m", "cm", "mm", "mi", "yd", "ft", "in")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLengthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        setupSpinner(binding.spinnerLength1)
        setupSpinner(binding.spinnerLength2)
        setupLengthConversion()
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
                convertLength()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateSpinnerDisplay(spinner: android.widget.Spinner, position: Int) {
        val selectedTextView = spinner.selectedView as? android.widget.TextView
        selectedTextView?.text = abbreviatedUnits[position]
    }

    private fun setupLengthConversion() {
        binding.spinnerLength1.setSelection(0)
        binding.spinnerLength2.setSelection(1)
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
        binding.lengthValue1.setOnClickListener {
            isEditingFirstValue = true
            highlightSelectedInput()
        }

        binding.lengthValue2.setOnClickListener {
            isEditingFirstValue = false
            highlightSelectedInput()
        }
    }

    private fun highlightSelectedInput() {
        val selectedColor = resources.getColor(R.color.operation, null)
        val defaultColor = resources.getColor(R.color.textPrimary, null)

        if (isEditingFirstValue) {
            binding.lengthValue1.setTextColor(selectedColor)
            binding.lengthValue2.setTextColor(defaultColor)
        } else {
            binding.lengthValue1.setTextColor(defaultColor)
            binding.lengthValue2.setTextColor(selectedColor)
        }
    }

    private fun appendToInput(value: String) {
        val inputField = if (isEditingFirstValue) binding.lengthValue1 else binding.lengthValue2
        var currentText = inputField.text.toString()

        // Prevent multiple decimals
        if (value == "." && currentText.contains(".")) return

        // Avoid leading zeros
        if (currentText == "0" && value != ".") {
            currentText = ""
        }

        currentText += value
        inputField.text = currentText
        convertLength()
    }

    private fun allClear() {
        binding.lengthValue1.text = "0"
        binding.lengthValue2.text = ""
    }

    private fun backspaceInput() {
        val inputField = if (isEditingFirstValue) binding.lengthValue1 else binding.lengthValue2
        val currentText = inputField.text.toString()
        if (currentText.isNotEmpty()) {
            inputField.text = currentText.dropLast(1)
        }
        if (inputField.text.isEmpty()) {
            inputField.text = "0"
        }
        convertLength()
    }

    private fun convertLength() {
        val input = (if (isEditingFirstValue) binding.lengthValue1.text else binding.lengthValue2.text)
            .toString()
            .toDoubleOrNull() ?: return
        val fromUnit = if (isEditingFirstValue) binding.spinnerLength1.selectedItem.toString() else binding.spinnerLength2.selectedItem.toString()
        val toUnit = if (isEditingFirstValue) binding.spinnerLength2.selectedItem.toString() else binding.spinnerLength1.selectedItem.toString()

        val result = when (fromUnit) {
            "Kilometer" -> convertFromKilometer(input, toUnit)
            "Meter" -> convertFromMeter(input, toUnit)
            "Centimeter" -> convertFromCentimeter(input, toUnit)
            "Millimeter" -> convertFromMillimeter(input, toUnit)
            "Mile" -> convertFromMile(input, toUnit)
            "Yard" -> convertFromYard(input, toUnit)
            "Foot" -> convertFromFoot(input, toUnit)
            "Inch" -> convertFromInch(input, toUnit)
            else -> input
        }

        if (isEditingFirstValue) {
            binding.lengthValue2.text = String.format("%.2f", result)
        } else {
            binding.lengthValue1.text = String.format("%.2f", result)
        }
    }

    // Conversion methods for length
    private fun convertFromKilometer(value: Double, toUnit: String): Double {
        return when (toUnit) {
            "Kilometer" -> value
            "Meter" -> value * 1000
            "Centimeter" -> value * 100_000
            "Millimeter" -> value * 1_000_000
            "Mile" -> value / 1.60934
            "Yard" -> value * 1093.61
            "Foot" -> value * 3280.84
            "Inch" -> value * 39_370.1
            else -> value
        }
    }

    private fun convertFromMeter(value: Double, toUnit: String): Double {
        return when (toUnit) {
            "Kilometer" -> value / 1000
            "Meter" -> value
            "Centimeter" -> value * 100
            "Millimeter" -> value * 1000
            "Mile" -> value / 1609.34
            "Yard" -> value * 1.09361
            "Foot" -> value * 3.28084
            "Inch" -> value * 39.3701
            else -> value
        }
    }

    private fun convertFromCentimeter(value: Double, toUnit: String): Double {
        return when (toUnit) {
            "Kilometer" -> value / 100_000
            "Meter" -> value / 100
            "Centimeter" -> value
            "Millimeter" -> value * 10
            "Mile" -> value / 160_934
            "Yard" -> value / 91.44
            "Foot" -> value / 30.48
            "Inch" -> value / 2.54
            else -> value
        }
    }

    private fun convertFromMillimeter(value: Double, toUnit: String): Double {
        return when (toUnit) {
            "Kilometer" -> value / 1_000_000
            "Meter" -> value / 1000
            "Centimeter" -> value / 10
            "Millimeter" -> value
            "Mile" -> value / 1_609_344
            "Yard" -> value / 914.4
            "Foot" -> value / 304.8
            "Inch" -> value / 25.4
            else -> value
        }
    }

    private fun convertFromMile(value: Double, toUnit: String): Double {
        return when (toUnit) {
            "Kilometer" -> value * 1.60934
            "Meter" -> value * 1609.34
            "Centimeter" -> value * 160_934
            "Millimeter" -> value * 1_609_344
            "Mile" -> value
            "Yard" -> value * 1760
            "Foot" -> value * 5280
            "Inch" -> value * 63_360
            else -> value
        }
    }

    private fun convertFromYard(value: Double, toUnit: String): Double {
        return when (toUnit) {
            "Kilometer" -> value / 1093.61
            "Meter" -> value / 1.09361
            "Centimeter" -> value * 91.44
            "Millimeter" -> value * 914.4
            "Mile" -> value / 1760
            "Yard" -> value
            "Foot" -> value * 3
            "Inch" -> value * 36
            else -> value
        }
    }

    private fun convertFromFoot(value: Double, toUnit: String): Double {
        return when (toUnit) {
            "Kilometer" -> value / 3280.84
            "Meter" -> value / 3.28084
            "Centimeter" -> value * 30.48
            "Millimeter" -> value * 304.8
            "Mile" -> value / 5280
            "Yard" -> value / 3
            "Foot" -> value
            "Inch" -> value * 12
            else -> value
        }
    }

    private fun convertFromInch(value: Double, toUnit: String): Double {
        return when (toUnit) {
            "Kilometer" -> value / 39_370.1
            "Meter" -> value / 39.3701
            "Centimeter" -> value * 2.54
            "Millimeter" -> value * 25.4
            "Mile" -> value / 63_360
            "Yard" -> value / 36
            "Foot" -> value / 12
            "Inch" -> value
            else -> value
        }
    }
}
