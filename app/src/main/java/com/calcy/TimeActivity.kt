package com.calcy

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.calcy.databinding.ActivityTimeBinding

class TimeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTimeBinding
    private var isEditingFirstValue: Boolean = true // Track which value is being edited

    // Full names and abbreviated units for time
    private val fullUnits = arrayOf("Year", "Week", "Day", "Hour", "Minute", "Second", "Millisecond")
    private val abbreviatedUnits = arrayOf("yr", "wk", "d", "hr", "min", "s", "ms")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        setupSpinner(binding.spinnerTime1)
        setupSpinner(binding.spinnerTime2)
        setupTimeConversion()
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
                convertTime()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateSpinnerDisplay(spinner: android.widget.Spinner, position: Int) {
        val selectedTextView = spinner.selectedView as? android.widget.TextView
        selectedTextView?.text = abbreviatedUnits[position]
    }

    private fun setupTimeConversion() {
        binding.spinnerTime1.setSelection(0)
        binding.spinnerTime2.setSelection(1)
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
        binding.timeValue1.setOnClickListener {
            isEditingFirstValue = true
            highlightSelectedInput()
        }

        binding.timeValue2.setOnClickListener {
            isEditingFirstValue = false
            highlightSelectedInput()
        }
    }

    private fun highlightSelectedInput() {
        val selectedColor = resources.getColor(R.color.operation, null)
        val defaultColor = resources.getColor(R.color.textPrimary, null)

        if (isEditingFirstValue) {
            binding.timeValue1.setTextColor(selectedColor)
            binding.timeValue2.setTextColor(defaultColor)
        } else {
            binding.timeValue1.setTextColor(defaultColor)
            binding.timeValue2.setTextColor(selectedColor)
        }
    }

    private fun appendToInput(value: String) {
        val inputField = if (isEditingFirstValue) binding.timeValue1 else binding.timeValue2
        var currentText = inputField.text.toString()

        // Prevent multiple decimals
        if (value == "." && currentText.contains(".")) return

        // Avoid leading zeros
        if (currentText == "0" && value != ".") {
            currentText = ""
        }

        currentText += value
        inputField.text = currentText
        convertTime()
    }

    private fun allClear() {
        binding.timeValue1.text = "0"
        binding.timeValue2.text = ""
    }

    private fun backspaceInput() {
        val inputField = if (isEditingFirstValue) binding.timeValue1 else binding.timeValue2
        val currentText = inputField.text.toString()
        if (currentText.isNotEmpty()) {
            inputField.text = currentText.dropLast(1)
        }
        if (inputField.text.isEmpty()) {
            inputField.text = "0"
        }
        convertTime()
    }

    private fun convertTime() {
        val input = (if (isEditingFirstValue) binding.timeValue1.text else binding.timeValue2.text)
            .toString()
            .toDoubleOrNull() ?: return
        val fromUnit = if (isEditingFirstValue) binding.spinnerTime1.selectedItem.toString() else binding.spinnerTime2.selectedItem.toString()
        val toUnit = if (isEditingFirstValue) binding.spinnerTime2.selectedItem.toString() else binding.spinnerTime1.selectedItem.toString()

        val result = when (fromUnit) {
            "Year" -> convertFromYear(input, toUnit)
            "Week" -> convertFromWeek(input, toUnit)
            "Day" -> convertFromDay(input, toUnit)
            "Hour" -> convertFromHour(input, toUnit)
            "Minute" -> convertFromMinute(input, toUnit)
            "Second" -> convertFromSecond(input, toUnit)
            "Millisecond" -> convertFromMillisecond(input, toUnit)
            else -> input
        }

        if (isEditingFirstValue) {
            binding.timeValue2.text = String.format("%.2f", result)
        } else {
            binding.timeValue1.text = String.format("%.2f", result)
        }
    }

    // Conversion methods for time
    private fun convertFromYear(value: Double, toUnit: String): Double {
        return when (toUnit) {
            "Year" -> value
            "Week" -> value * 52.1429
            "Day" -> value * 365
            "Hour" -> value * 365 * 24
            "Minute" -> value * 365 * 24 * 60
            "Second" -> value * 365 * 24 * 60 * 60
            "Millisecond" -> value * 365 * 24 * 60 * 60 * 1000
            else -> value
        }
    }

    private fun convertFromWeek(value: Double, toUnit: String): Double {
        return when (toUnit) {
            "Year" -> value / 52.1429
            "Week" -> value
            "Day" -> value * 7
            "Hour" -> value * 7 * 24
            "Minute" -> value * 7 * 24 * 60
            "Second" -> value * 7 * 24 * 60 * 60
            "Millisecond" -> value * 7 * 24 * 60 * 60 * 1000
            else -> value
        }
    }

    private fun convertFromDay(value: Double, toUnit: String): Double {
        return when (toUnit) {
            "Year" -> value / 365
            "Week" -> value / 7
            "Day" -> value
            "Hour" -> value * 24
            "Minute" -> value * 24 * 60
            "Second" -> value * 24 * 60 * 60
            "Millisecond" -> value * 24 * 60 * 60 * 1000
            else -> value
        }
    }

    private fun convertFromHour(value: Double, toUnit: String): Double {
        return when (toUnit) {
            "Year" -> value / (365 * 24)
            "Week" -> value / (7 * 24)
            "Day" -> value / 24
            "Hour" -> value
            "Minute" -> value * 60
            "Second" -> value * 60 * 60
            "Millisecond" -> value * 60 * 60 * 1000
            else -> value
        }
    }

    private fun convertFromMinute(value: Double, toUnit: String): Double {
        return when (toUnit) {
            "Year" -> value / (365 * 24 * 60)
            "Week" -> value / (7 * 24 * 60)
            "Day" -> value / (24 * 60)
            "Hour" -> value / 60
            "Minute" -> value
            "Second" -> value * 60
            "Millisecond" -> value * 60 * 1000
            else -> value
        }
    }

    private fun convertFromSecond(value: Double, toUnit: String): Double {
        return when (toUnit) {
            "Year" -> value / (365 * 24 * 60 * 60)
            "Week" -> value / (7 * 24 * 60 * 60)
            "Day" -> value / (24 * 60 * 60)
            "Hour" -> value / (60 * 60)
            "Minute" -> value / 60
            "Second" -> value
            "Millisecond" -> value * 1000
            else -> value
        }
    }

    private fun convertFromMillisecond(value: Double, toUnit: String): Double {
        return when (toUnit) {
            "Year" -> value / (365 * 24 * 60 * 60 * 1000)
            "Week" -> value / (7 * 24 * 60 * 60 * 1000)
            "Day" -> value / (24 * 60 * 60 * 1000)
            "Hour" -> value / (60 * 60 * 1000)
            "Minute" -> value / (60 * 1000)
            "Second" -> value / 1000
            "Millisecond" -> value
            else -> value
        }
    }
}
