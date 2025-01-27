package com.example.calculator.data.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OperationCheckerTest {

    @Test
    fun `isOperation returns true for valid operations`() {
        assertTrue(OperationChecker.isOperation("+"))
        assertTrue(OperationChecker.isOperation("-"))
        assertTrue(OperationChecker.isOperation("/"))
        assertTrue(OperationChecker.isOperation("×"))
    }

    @Test
    fun `isOperation returns false for invalid operations`() {
        assertFalse(OperationChecker.isOperation("^"))
        assertFalse(OperationChecker.isOperation("C"))
        assertFalse(OperationChecker.isOperation("3"))
        assertFalse(OperationChecker.isOperation("."))
        assertFalse(OperationChecker.isOperation("="))
        assertFalse(OperationChecker.isOperation("("))
        assertFalse(OperationChecker.isOperation(")"))
    }

    @Test
    fun `isPriorityOperation respects operator priorities`() {
        assertTrue(OperationChecker.isPriorityOperation("×", "-"))
        assertTrue(OperationChecker.isPriorityOperation("/", "-"))
        assertTrue(OperationChecker.isPriorityOperation("/", "+"))
        assertTrue(OperationChecker.isPriorityOperation("×", "+"))

        assertFalse(OperationChecker.isPriorityOperation("×", "/"))
        assertFalse(OperationChecker.isPriorityOperation("-", "+"))

        assertFalse(OperationChecker.isPriorityOperation("-", "×"))
        assertFalse(OperationChecker.isPriorityOperation("-", "/"))
        assertFalse(OperationChecker.isPriorityOperation("+", "×"))
        assertFalse(OperationChecker.isPriorityOperation("+", "/"))
    }
}