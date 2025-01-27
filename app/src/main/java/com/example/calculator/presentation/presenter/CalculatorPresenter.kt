package com.example.calculator.presentation.presenter

import com.example.calculator.data.model.ExpressionParser

class CalculatorPresenter : CalculatorContract.Presenter {

    private var view: CalculatorContract.View? = null
    private var currentExpression: String = ""

    override fun attachView(view: CalculatorContract.View) {
        this.view = view
    }

    override fun detachView() {
        view = null
    }

    override fun onDigitPressed(digit: String) {
        if (canAddCharacter(digit)) {
            currentExpression = formatResult(currentExpression, true) + digit
            view?.showResult(currentExpression)
        }
    }

    override fun onOperatorPressed(operator: String) {
        if (canAddCharacter(operator)) {
            currentExpression = formatResult(currentExpression) + operator
            view?.showResult(currentExpression)
        }
    }

    override fun onPointPressed(point: String) {
        if (canAddCharacter(point)) {
            currentExpression = formatResult(currentExpression) + point
            view?.showResult(currentExpression)
        }
    }

    override fun onParenthesisPressed(parenthesis: String) {
        if (canAddCharacter(parenthesis)) {
            currentExpression = formatResult(currentExpression) + parenthesis
            view?.showResult(currentExpression)
        }
    }

    override fun onCalculate(expression: String) {
        try {
            var result = ExpressionParser.calculate(expression).toString()
            result = formatResult(result)
            view?.showResult(result)
            currentExpression = result
        } catch (e: Exception) {
            view?.showError("${e.message}")
        }
    }

    override fun onClear() {
        view?.clearResult()
    }

    override fun onClearAll() {
        view?.clearAllResult()
    }

    override fun updateCurrentExpression(updatedExpression: String) {
        currentExpression = updatedExpression
    }

    override fun formatResult(result: String, formatBeforeDigit: Boolean): String {
        return if (!formatBeforeDigit && result.endsWith(".0")) {
            result.dropLast(2)
        } else {
            val lastNumber = result.takeLastWhile { it.isDigit() || it == '.' }
            val pointIndex = lastNumber.indexOf('.')
            if (pointIndex == -1) return result

            val part1 = lastNumber.substring(0, pointIndex)
            val part2 = lastNumber.substring(pointIndex + 1)
            val lengthOfPartBeforeLastNumber = result.length - lastNumber.length
            val part0 = if (lengthOfPartBeforeLastNumber > 0) {
                result.substring(0, lengthOfPartBeforeLastNumber)
            } else ""

            "$part0$part1.${part2.take(10)}"
        }
    }

    override fun canAddCharacter(ch: String): Boolean {
        val lastCh = currentExpression.lastOrNull()

        return when (ch){
            "(" -> {
                lastCh == null || lastCh in "+-×/("
            }
            ")" -> {
                if (lastCh == null || lastCh in "+-×/(") false
                else currentExpression.count { it == '(' } > currentExpression.count { it == ')' }
            }
            "." -> {
                if (lastCh == null || lastCh in "+-×/().") false
                else !currentExpression.takeLastWhile { it.isDigit() || it == '.' }.contains('.')
            }
            "-" -> {
                lastCh == null || lastCh !in "+-×/."
            }
            in "+×/" -> {
                lastCh != null && lastCh !in "+-×/.("
            }
            else -> {
                val lastSegment = currentExpression.takeLastWhile { it.isDigit() || it == '.' }
                if (lastSegment.contains('.')) {
                    val digitsAfterPoint = lastSegment.substringAfter('.').length
                    val totalDigits = lastSegment.length - 1

                    (digitsAfterPoint < 10 && totalDigits < 15).also { isValid ->
                        if (!isValid) {
                            if (digitsAfterPoint >= 10) {
                                view?.showError("Невозможно ввести более 10 цифр после точки")
                            } else {
                                view?.showError("Невозможно ввести более 15 цифр")
                            }
                        }
                    }
                } else {
                    val totalDigits = currentExpression.takeLastWhile { it.isDigit() }.length

                    (totalDigits < 15).also { isValid ->
                        if (!isValid) view?.showError("Невозможно ввести более 15 цифр")
                    }
                }
            }
        }
    }
}
