package com.example.calculator.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class ExpressionParserTest {

    companion object {
        private const val EPSILON = 1e-9
    }

    // Tests for isValidExpression
    @Test
    fun `isValidExpression returns true for valid expressions`() {
        val validExpressions = listOf(
            "5+6",
            "3.485+2",
            "-1+9",
            "3+(-5)",
            "(63+75)×2",
            "(12+(4-3))",
            "1+(2×(5-1))",
            "5.5+4.4",
            "-(5-9)",
            "3-(-5.7+2)"
        )

        validExpressions.forEach { expression ->
            assertTrue("Expected '$expression' to be valid", ExpressionParser.isValidExpression(expression))
        }
    }

    @Test
    fun `isValidExpression handles excessive parentheses`() {
        val validExpression = "((((3+5))))"
        assertTrue("Expected '$validExpression' to be valid with excessive parentheses",
            ExpressionParser.isValidExpression(validExpression))
    }

    @Test
    fun `isValidExpression returns false for unbalanced parentheses`() {
        val invalidExpressions = listOf(
            "(1+2",
            "1+2)",
            "(1+(2×3)",
            "((1+2)"
        )

        invalidExpressions.forEach { expression ->
            assertFalse("Expected '$expression' to be invalid due to unbalanced parentheses",
                ExpressionParser.isValidExpression(expression))
        }
    }

    @Test
    fun `isValidExpression returns false for invalid operator usage`() {
        val invalidExpressions = listOf(
            "1++2",
            "1+×2",
            "1+-2",
            "1+2-",
            "1×",
            "+1",
            "×1",
            "/1",
            "2(2+2)",
            "1+()+2"
        )

        invalidExpressions.forEach { expression ->
            assertFalse("Expected '$expression' to be invalid due to invalid operator usage",
                ExpressionParser.isValidExpression(expression))
        }
    }

    @Test
    fun `isValidExpression returns false for multiple dots in a number`() {
        val invalidExpressions = listOf(
            "1.5.2+3",
            "1..5-6",
            "1+2.2.1"
        )

        invalidExpressions.forEach { expression ->
            assertFalse("Expected '$expression' to be invalid due to multiple dots in a number",
                ExpressionParser.isValidExpression(expression))
        }
    }

    @Test
    fun `isValidExpression handles empty expression`() {
        assertFalse("Expected an empty expression to be invalid",
            ExpressionParser.isValidExpression(""))
    }

    // Tests for separateString
    @Test
    fun `separateString handles simple expression`() {
        val result = ExpressionParser.separateString("3+5×2/(1-4)")
        val expected = arrayOf("3", "+", "5", "×", "2", "/", "(", "1", "-", "4", ")")
        assertEquals(expected.toList(), result.toList())
    }

    @Test
    fun `separateString handles decimal numbers`() {
        val result = ExpressionParser.separateString("(3.5+2.7)×1.1")
        val expected = arrayOf("(", "3.5", "+", "2.7", ")", "×", "1.1")
        assertEquals(expected.toList(), result.toList())
    }

    // Tests for translateInfixToPostfix
    @Test
    fun `translateInfixToPostfix converts simple expression without parentheses`() {
        val result = ExpressionParser.translateInfixToPostfix("3+5-2")
        val expected = arrayOf("3", "5", "+", "2", "-")
        assertEquals(expected.toList(), result.toList())
    }

    @Test
    fun `translateInfixToPostfix respects operator precedence`() {
        val result = ExpressionParser.translateInfixToPostfix("3+5×2-4/2")
        val expected = arrayOf("3", "5", "2", "×", "+", "4", "2", "/", "-")
        assertEquals(expected.toList(), result.toList())
    }

    @Test
    fun `translateInfixToPostfix handles nested parentheses`() {
        val result = ExpressionParser.translateInfixToPostfix("(3+(5×2))/(1-4)")
        val expected = arrayOf("3", "5", "2", "×", "+", "1", "4", "-", "/")
        assertEquals(expected.toList(), result.toList())
    }

    @Test
    fun `translateInfixToPostfix handles simple nested parentheses`() {
        val result = ExpressionParser.translateInfixToPostfix("(1-4)")
        val expected = arrayOf("1", "4", "-")
        assertEquals(expected.toList(), result.toList())
    }

    @Test
    fun `translateInfixToPostfix handles unary minus in simple expressions`() {
        val result = ExpressionParser.translateInfixToPostfix("-3+5")
        val expected = arrayOf("0", "3", "-", "5", "+")
        assertEquals(expected.toList(), result.toList())
    }

    @Test
    fun `translateInfixToPostfix handles unary minus in nested expressions`() {
        val result = ExpressionParser.translateInfixToPostfix("3-(-5-2)")
        val expected = arrayOf("3", "0", "5", "-", "2", "-", "-")
        assertEquals(expected.toList(), result.toList())
    }

    // Tests for calculate
    @Test
    fun `calculate handles simple addition`() {
        val result = ExpressionParser.calculate("2+3")
        val expected = 5.0
        assertEquals(expected, result, EPSILON)
    }

    @Test
    fun `calculate handles multiple subtractions`() {
        val result = ExpressionParser.calculate("2-3-5")
        val expected = -6.0
        assertEquals(expected, result, EPSILON)
    }

    @Test
    fun `calculate handles operator precedence`() {
        val result = ExpressionParser.calculate("3+5×2")
        val expected = 13.0
        assertEquals(expected, result, EPSILON)
    }

    @Test
    fun `calculate handles parentheses`() {
        val result = ExpressionParser.calculate("(3+5)×2")
        val expected = 16.0
        assertEquals(expected, result, EPSILON)
    }

    @Test
    fun `calculate handles division`() {
        val result = ExpressionParser.calculate("10/2")
        val expected = 5.0
        assertEquals(expected, result, EPSILON)
    }

    @Test
    fun `calculate handles nested parentheses`() {
        val result = ExpressionParser.calculate("3+(5×(2+1))")
        val expected = 18.0
        assertEquals(expected, result, EPSILON)
    }

    @Test
    fun `calculate handles unary minus at the beginning of the expression`() {
        val result = ExpressionParser.calculate("-3+5")
        val expected = 2.0
        assertEquals(expected, result, EPSILON)
    }

    @Test
    fun `calculate evaluates nested parentheses with unary minus correctly`() {
        val result = ExpressionParser.calculate("3-(-5-2)")
        val expected = 10.0
        assertEquals(expected, result, EPSILON)
    }

    @Test
    fun `calculate throws exception for division by zero`() {
        assertThrows(ArithmeticException::class.java) {
            ExpressionParser.calculate("10/0")
        }
    }

    @Test
    fun `calculate handles unbalanced parentheses`() {
        assertThrows(IllegalArgumentException::class.java) {
            ExpressionParser.calculate("(3+5")
        }
    }

    @Test
    fun `calculate handles invalid parentheses`() {
        assertThrows(IllegalArgumentException::class.java) {
            ExpressionParser.calculate("1+()")
        }
    }

    @Test
    fun `calculate handles simple expression with double numbers`() {
        val result = ExpressionParser.calculate("6.4/2+3.3")
        val expected = 6.5
        assertEquals(expected, result, EPSILON)
    }

    @Test
    fun `calculate handles complex expression with double numbers`() {
        val result = ExpressionParser.calculate("(3.0+(5.0×2.0))/(1.0-4.0)")
        val expected = -4.33333333333333
        assertEquals(expected, result, EPSILON)
    }

    @Test
    fun `calculate throws exception for invalid syntax`() {
        assertThrows(IllegalArgumentException::class.java) {
            ExpressionParser.calculate("3+×5")
        }
    }

    @Test
    fun `calculate throws exception for invalid ending`() {
        assertThrows(IllegalArgumentException::class.java) {
            ExpressionParser.calculate("3+5-")
        }
    }

    @Test
    fun `calculate throws exception for multiple dots in number`() {
        assertThrows(IllegalArgumentException::class.java) {
            ExpressionParser.calculate("3.5.2+1")
        }
    }
    
    @Test
    fun `calculate handles zero operations`() {
        val results = listOf(
            ExpressionParser.calculate("0+0") to 0.0,
            ExpressionParser.calculate("0×5") to 0.0,
            ExpressionParser.calculate("5+0") to 5.0
        )

        results.forEach { (result, expected) ->
            assertEquals(expected, result, EPSILON)
        }
    }
}
