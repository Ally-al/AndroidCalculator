package com.example.calculator.presentation.presenter

interface CalculatorContract {

    interface View {
        fun showResult(result: String)

        fun showError(message: String)

        fun clearResult()

        fun clearAllResult()
    }

    interface Presenter {
        fun attachView(view: View)

        fun detachView()

        fun onDigitPressed(digit: String)

        fun onOperatorPressed(operator: String)

        fun onPointPressed(point: String)

        fun onParenthesisPressed(parenthesis: String)

        fun onCalculate(expression: String)

        fun onClear()

        fun onClearAll()

        fun updateCurrentExpression(updatedExpression: String)

        fun formatResult(result: String, formatBeforeDigit: Boolean = false): String

        fun canAddCharacter(ch: String) : Boolean
    }
}
