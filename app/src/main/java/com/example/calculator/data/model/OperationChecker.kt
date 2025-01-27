package com.example.calculator.data.model

object OperationChecker {

    fun isOperation(el: String) : Boolean {
        return el in setOf("+", "-", "×", "/")
    }

    private fun determinePriority(el: String) : Int {
        return when (el) {
            "+", "-" -> 1
            "×", "/" -> 2
            else -> 0
        }
    }

    fun isPriorityOperation(firstOp: String, secondOp: String) : Boolean {
        return determinePriority(firstOp) > determinePriority(secondOp)
    }
}
