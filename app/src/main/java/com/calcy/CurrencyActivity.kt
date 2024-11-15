package com.calcy

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.calcy.databinding.ActivityCurrencyBinding
import org.json.JSONObject

class CurrencyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCurrencyBinding
    private val baseUrl = "https://api.frankfurter.app/latest"
    private var rates = mutableMapOf<String, Double>()
    private var isEditingFirstValue: Boolean = true // Track which value is being edited

    private val fullCurrencyList = arrayOf(
        "USD - United States Dollar",
        "AUD - Australian Dollar",
        "BGN - Bulgarian Lev",
        "BRL - Brazilian Real",
        "CAD - Canadian Dollar",
        "CHF - Swiss Franc",
        "CNY - Chinese Yuan",
        "CZK - Czech Koruna",
        "DKK - Danish Krone",
        "EUR - Euro",
        "GBP - British Pound Sterling",
        "HKD - Hong Kong Dollar",
        "HUF - Hungarian Forint",
        "IDR - Indonesian Rupiah",
        "ILS - Israeli New Shekel",
        "INR - Indian Rupee",
        "ISK - Icelandic Krona",
        "JPY - Japanese Yen",
        "KRW - South Korean Won",
        "MXN - Mexican Peso",
        "MYR - Malaysian Ringgit",
        "NOK - Norwegian Krone",
        "NZD - New Zealand Dollar",
        "PHP - Philippine Peso",
        "PLN - Polish Zloty",
        "RON - Romanian Leu",
        "SEK - Swedish Krona",
        "SGD - Singapore Dollar",
        "THB - Thai Baht",
        "TRY - Turkish Lira",
        "ZAR - South African Rand"
    )

    private val currencyCodes = fullCurrencyList.map { it.split(" - ")[0] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCurrencyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupSpinners()
        setupButtonListeners()
        setupFocusListeners()
        fetchCurrencyRates()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupSpinners() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, fullCurrencyList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCurrency1.adapter = adapter
        binding.spinnerCurrency2.adapter = adapter

        binding.spinnerCurrency1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateSpinnerDisplay(binding.spinnerCurrency1, position)
                convertCurrency()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.spinnerCurrency2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateSpinnerDisplay(binding.spinnerCurrency2, position)
                convertCurrency()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateSpinnerDisplay(spinner: android.widget.Spinner, position: Int) {
        val selectedTextView = spinner.selectedView as? TextView
        selectedTextView?.post {
            selectedTextView.text = currencyCodes[position]
        }
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
        binding.currencyValue1.setOnClickListener {
            isEditingFirstValue = true
            highlightSelectedInput()
        }

        binding.currencyValue2.setOnClickListener {
            isEditingFirstValue = false
            highlightSelectedInput()
        }
    }

    private fun highlightSelectedInput() {
        val selectedColor = resources.getColor(R.color.operation, null)
        val defaultColor = resources.getColor(R.color.textPrimary, null)

        if (isEditingFirstValue) {
            binding.currencyValue1.setTextColor(selectedColor)
            binding.currencyValue2.setTextColor(defaultColor)
        } else {
            binding.currencyValue1.setTextColor(defaultColor)
            binding.currencyValue2.setTextColor(selectedColor)
        }
    }

    private fun appendToInput(value: String) {
        val inputField = if (isEditingFirstValue) binding.currencyValue1 else binding.currencyValue2
        var currentText = inputField.text.toString()

        if (value == "." && currentText.contains(".")) return

        if (currentText == "0" && value != ".") {
            currentText = ""
        }

        currentText += value
        inputField.text = currentText
        convertCurrency()
    }

    private fun allClear() {
        binding.currencyValue1.text = "0"
        binding.currencyValue2.text = ""
    }

    private fun backspaceInput() {
        val inputField = if (isEditingFirstValue) binding.currencyValue1 else binding.currencyValue2
        val currentText = inputField.text.toString()
        if (currentText.isNotEmpty()) {
            inputField.text = currentText.dropLast(1)
        }
        if (inputField.text.isEmpty()) {
            inputField.text = "0"
        }
        convertCurrency()
    }

    private fun fetchCurrencyRates() {
        val queue = Volley.newRequestQueue(this)
        val url = "$baseUrl?base=USD"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                handleApiResponse(response)
            },
            {
                Toast.makeText(this, "Failed to fetch currency rates", Toast.LENGTH_SHORT).show()
            }
        )

        queue.add(jsonObjectRequest)
    }

    private fun handleApiResponse(response: JSONObject) {
        val ratesJson = response.getJSONObject("rates")
        rates.clear()
        for (key in ratesJson.keys()) {
            rates[key] = ratesJson.getDouble(key)
        }
        rates["USD"] = 1.0 // Ensure USD base rate is set
        convertCurrency()
    }

    private fun convertCurrency() {
        val input = (if (isEditingFirstValue) binding.currencyValue1.text else binding.currencyValue2.text)
            .toString()
            .toDoubleOrNull() ?: return
        val fromCurrency = if (isEditingFirstValue) currencyCodes[binding.spinnerCurrency1.selectedItemPosition] else currencyCodes[binding.spinnerCurrency2.selectedItemPosition]
        val toCurrency = if (isEditingFirstValue) currencyCodes[binding.spinnerCurrency2.selectedItemPosition] else currencyCodes[binding.spinnerCurrency1.selectedItemPosition]

        val result = if (rates.containsKey(fromCurrency) && rates.containsKey(toCurrency)) {
            val fromRate = rates[fromCurrency] ?: 1.0
            val toRate = rates[toCurrency] ?: 1.0
            input / fromRate * toRate
        } else {
            input
        }

        if (isEditingFirstValue) {
            binding.currencyValue2.text = String.format("%.2f", result)
        } else {
            binding.currencyValue1.text = String.format("%.2f", result)
        }
    }
}
