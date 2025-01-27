package com.example.calculator.presentation.presenter

import com.example.calculator.presentation.presenter.CalculatorContract.View
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify


class CalculatorPresenterTest {
    private lateinit var presenter: CalculatorPresenter
    private val mockView: View = mock()

    @Before
    fun setUp() {
        presenter = CalculatorPresenter()
        presenter.attachView(mockView)
    }

    // onDigitPressed

    @Test
    fun `onDigitPressed adds digit to expression and updates view`() {
        presenter.onDigitPressed("1")
        verify(mockView).showResult("1")
    }

    @Test
    fun `onDigitPressed shows error when entering more than 10 digits after the point`() {
        presenter.onDigitPressed("1")
        presenter.onPointPressed(".")
        repeat(10) { presenter.onDigitPressed("1") }
        presenter.onDigitPressed("2")
        verify(mockView).showError("Невозможно ввести более 10 цифр после точки")
    }

    @Test
    fun `onDigitPressed shows error when entering more than 15 digits`() {
        repeat(15) { presenter.onDigitPressed("1") }
        presenter.onDigitPressed("2")
        verify(mockView).showError("Невозможно ввести более 15 цифр")
    }

    // onOperatorPressed

    @Test
    fun `onOperatorPressed adds operator if valid and updates view`() {
        presenter.onDigitPressed("5")
        presenter.onOperatorPressed("+")
        verify(mockView).showResult("5+")
    }

    @Test
    fun `onOperatorPressed does not add operator if invalid`() {
        presenter.onOperatorPressed("+")
        verify(mockView, never()).showResult(any())
    }

    // onPointPressed

    @Test
    fun `onPointPressed adds point if valid and updates view`() {
        presenter.onDigitPressed("5")
        presenter.onPointPressed(".")
        verify(mockView).showResult("5.")
    }

    @Test
    fun `onPointPressed does not add point if invalid`() {
        presenter.onDigitPressed("5")
        presenter.onPointPressed(".")
        presenter.onPointPressed(".")
        verify(mockView, times(1)).showResult("5.")
    }

    // onParenthesisPressed

    @Test
    fun `onParenthesisPressed adds parenthesis if valid and updates view`() {
        presenter.onParenthesisPressed("(")
        verify(mockView).showResult("(")
    }

    @Test
    fun `onParenthesisPressed does not add closing parenthesis if invalid`() {
        presenter.onParenthesisPressed(")")
        verify(mockView, never()).showResult(any())
    }

    @Test
    fun `onParenthesisPressed rejects invalid closing parentheses`() {
        presenter.onParenthesisPressed("(")
        presenter.onParenthesisPressed(")")
        presenter.onParenthesisPressed(")")
        verify(mockView, times(1)).showResult("(")
    }

    // onCalculate

    @Test
    fun `onCalculate computes result and updates view`() {
        presenter.onDigitPressed("5")
        presenter.onOperatorPressed("+")
        presenter.onDigitPressed("3")
        presenter.onCalculate("5+3")
        verify(mockView).showResult("8")
    }

    @Test
    fun `onCalculate shows error for invalid expression`() {
        presenter.onDigitPressed("5")
        presenter.onOperatorPressed("+")
        presenter.onCalculate("5+")
        verify(mockView).showError(any())
    }

    // onClear

    @Test
    fun `onClear updates view and does not affect full expression`() {
        presenter.onDigitPressed("2")
        presenter.onOperatorPressed("+")
        presenter.onClear()
        verify(mockView).clearResult()
    }

    // onClearAll

    @Test
    fun `onClearAll clears entire expression and updates view`() {
        presenter.onDigitPressed("2")
        presenter.onOperatorPressed("+")
        presenter.onClearAll()
        verify(mockView).clearAllResult()
    }

    // formatResult

    @Test
    fun `formatResult should format result correctly for whole numbers`() {
        val result = presenter.formatResult("12345.0")
        assertEquals("12345", result)
    }

    @Test
    fun `formatResult should not format result for correct decimal numbers`() {
        val result = presenter.formatResult("12345.67")
        assertEquals("12345.67", result)
    }

    @Test
    fun `formatResult truncates decimals to 10 digits`() {
        val result = presenter.formatResult("12345.1234567890123")
        assertEquals("12345.1234567890", result)
    }

    @Test
    fun `formatResult when expression is not full`() {
        val result = presenter.formatResult("1.444444444×0.")
        assertEquals("1.444444444×0.", result)
    }

    @Test
    fun `formatResult before digit adds to expression`() {
        val result = presenter.formatResult("0.0", true)
        assertEquals("0.0", result)
    }

    // canAddCharacter

    @Test
    fun `canAddCharacter allows operator after digit`() {
        presenter.onDigitPressed("5")
        assert(presenter.canAddCharacter("+"))
    }

    @Test
    fun `canAddCharacter rejects operator plus at start`() {
        assert(!presenter.canAddCharacter("+"))
    }

    @Test
    fun `canAddCharacter rejects invalid inputs`() {
        presenter.onOperatorPressed("-")
        assert(!presenter.canAddCharacter("+"))
    }

    @Test
    fun `canAddCharacter rejects multiple points in one number`() {
        presenter.onDigitPressed("1")
        presenter.onPointPressed(".")
        assert(!presenter.canAddCharacter("."))
    }

    //

    @Test
    fun `expression with first number having 9 decimal places displays correctly`() {
        // "1.444444444*0.1"
        presenter.onDigitPressed("1")
        presenter.onPointPressed(".")
        repeat(9) { presenter.onDigitPressed("4") }
        presenter.onOperatorPressed("×")
        presenter.onDigitPressed("0")
        presenter.onPointPressed(".")
        presenter.onDigitPressed("1")

        verify(mockView).showResult("1.444444444×0.1")
    }
}
