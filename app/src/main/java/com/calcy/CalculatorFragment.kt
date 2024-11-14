package com.calcy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.calcy.databinding.FragmentCalculatorBinding

class CalculatorFragment : Fragment() {

    private var _binding: FragmentCalculatorBinding? = null
    private val binding get() = _binding!!

    private var currentNumber: String = ""
    private var operator: Operator? = null
    private var previousNumber: String = ""
    private var fullExpression: String = ""
    private var history: MutableList<String> = mutableListOf()
    private var justCalculated: Boolean = false

    enum class Operator {
        ADDITION, SUBTRACTION, MULTIPLICATION, DIVISION
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalculatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize button listeners
        binding.buttonZero.setOnClickListener { appendNumber("0") }
        binding.buttonOne.setOnClickListener { appendNumber("1") }
        binding.buttonTwo.setOnClickListener { appendNumber("2") }
        binding.buttonThree.setOnClickListener { appendNumber("3") }
        binding.buttonFour.setOnClickListener { appendNumber("4") }
        binding.buttonFive.setOnClickListener { appendNumber("5") }
        binding.buttonSix.setOnClickListener { appendNumber("6") }
        binding.buttonSeven.setOnClickListener { appendNumber("7") }
        binding.buttonEight.setOnClickListener { appendNumber("8") }
        binding.buttonNine.setOnClickListener { appendNumber("9") }
        binding.buttonDecimal.setOnClickListener { appendDecimal() }

        // Operation buttons
        binding.buttonAddition.setOnClickListener { setOperator(Operator.ADDITION, "+") }
        binding.buttonSubtraction.setOnClickListener { setOperator(Operator.SUBTRACTION, "-") }
        binding.buttonMultiple.setOnClickListener { setOperator(Operator.MULTIPLICATION, "×") }
        binding.buttonDivision.setOnClickListener { setOperator(Operator.DIVISION, "÷") }

        // Special buttons
        binding.buttonClear.setOnClickListener { clearCurrent() }
        binding.buttonEqual.setOnClickListener { calculateResult() }
        binding.buttonAllClear.setOnClickListener { allClear() }
        binding.backspace.setOnClickListener { backspace() }

        // Percentage button
        binding.buttonPercentage.setOnClickListener { calculatePercentage() }
    }

    private fun appendNumber(number: String) {
        if (justCalculated) {
            // If a result was just calculated, reset and start fresh
            currentNumber = number
            fullExpression = number
            justCalculated = false
        } else {
            currentNumber += number
            fullExpression += number
        }
        updateDisplay()
    }

    private fun appendDecimal() {
        if (!currentNumber.contains(".")) {
            currentNumber = if (currentNumber.isEmpty()) "0." else currentNumber + "."
            fullExpression += "."
        }
        updateDisplay()
    }

    private fun setOperator(selectedOperator: Operator, symbol: String) {
        if (currentNumber.isEmpty() && previousNumber.isEmpty()) {
            // No number input; ignore operator press
            return
        }

        if (justCalculated) {
            previousNumber = currentNumber
            fullExpression = "$currentNumber $symbol"
            justCalculated = false
        } else {
            if (currentNumber.isNotEmpty()) {
                if (operator != null) {
                    calculateIntermediateResult()
                } else {
                    previousNumber = currentNumber
                }
                fullExpression += " $symbol"
            } else if (operator != null) {
                fullExpression = fullExpression.dropLast(1) + symbol
            }
        }

        currentNumber = ""
        operator = selectedOperator
        updateDisplay()
    }

    private fun calculatePercentage() {
        if (currentNumber.isNotEmpty()) {
            val value = currentNumber.toDoubleOrNull()
            if (value != null) {
                currentNumber = if (operator != null && previousNumber.isNotEmpty()) {
                    // Calculate as a percentage of the previous number
                    val base = previousNumber.toDoubleOrNull() ?: 0.0
                    (base * (value / 100)).toString()
                } else {
                    // Convert to a percentage (divide by 100)
                    (value / 100).toString()
                }
                fullExpression += "%"
                updateDisplay()
            }
        }
    }

    private fun backspace() {
        if (currentNumber.isNotEmpty()) {
            currentNumber = currentNumber.dropLast(1)
            fullExpression = fullExpression.dropLast(1)
            updateDisplay()
        }
    }

    private fun clearCurrent() {
        currentNumber = ""
        updateDisplay()
    }

    private fun allClear() {
        currentNumber = ""
        previousNumber = ""
        operator = null
        fullExpression = ""
        justCalculated = false
        history.clear()
        updateHistory()
        updateDisplay()
    }

    private fun calculateResult() {
        if (operator != null && currentNumber.isNotEmpty()) {
            val result = performOperation(previousNumber.toDouble(), currentNumber.toDouble(), operator!!)
            history.add("$fullExpression = $result")
            updateHistory()

            // Update state for next calculation
            fullExpression = result.toString()
            currentNumber = result.toString()
            operator = null
            previousNumber = currentNumber
            justCalculated = true
            updateDisplay()
        }
    }

    private fun calculateIntermediateResult() {
        if (operator != null && currentNumber.isNotEmpty() && previousNumber.isNotEmpty()) {
            val result = performOperation(previousNumber.toDouble(), currentNumber.toDouble(), operator!!)
            previousNumber = result.toString()
            fullExpression = previousNumber
            currentNumber = ""
            updateDisplay()
        }
    }

    private fun performOperation(num1: Double, num2: Double, operation: Operator): Double {
        return when (operation) {
            Operator.ADDITION -> num1 + num2
            Operator.SUBTRACTION -> num1 - num2
            Operator.MULTIPLICATION -> num1 * num2
            Operator.DIVISION -> if (num2 != 0.0) num1 / num2 else Double.NaN
        }
    }

    private fun updateDisplay() {
        binding.numberInput.text = fullExpression
        binding.numberOutput.text = currentNumber
    }

    private fun updateHistory() {
        val historyText = history.joinToString("\n")
        binding.nummberHistory.text = historyText
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
