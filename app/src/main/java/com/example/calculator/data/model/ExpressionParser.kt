package com.example.calculator.data.model

object ExpressionParser {

    fun isValidExpression(expression: String) : Boolean {
        if (expression.isEmpty()) return false

        var balance = 0
        var lastCh: Char? = null
        var dotAllowed = true

        for (ch in expression) {
            when {
                ch == '(' -> {
                    if (lastCh != null && (lastCh == '.' || lastCh.isDigit())) return false
                    balance++
                }
                ch == ')' -> {
                    if (lastCh == null || lastCh in "+-×/.(") return false
                    balance--
                    if (balance < 0) return false
                }
                ch in "+×/" -> {
                    if (lastCh == null || lastCh in "+-×/(.") return false
                    dotAllowed = true
                }
                ch == '-' -> {
                    if (lastCh != null && lastCh in "+-×/.") return false
                    dotAllowed = true
                }
                ch == '.' -> {
                    if (!dotAllowed) return false
                    dotAllowed = false
                }
                ch.isDigit() -> Unit
                else -> return false
            }
            lastCh = ch
        }
        return balance == 0 && lastCh!! !in "+-×/("
    }

    fun separateString(expression: String) : ArrayDeque<String> {
        val elements: ArrayDeque<String> = ArrayDeque()
        var el = ""
        for (ch in expression) {
            if (ch.isDigit() || ch == '.') el += ch
            else {
                if (el.isNotEmpty()) elements.add(el)
                elements.add(ch.toString())
                el = ""
            }
        }
        if (el.isNotEmpty()) elements.add(el)
        return elements
    }

    fun translateInfixToPostfix(expression: String): ArrayDeque<String> {
        val q1: ArrayDeque<String> = separateString(expression)
        val q2: ArrayDeque<String> = ArrayDeque()
        val st: ArrayDeque<String> = ArrayDeque()

        var prevEl = ""

        while (q1.isNotEmpty()) {
            val el: String = q1.removeFirst()

            when {
                el == "(" -> st.add(el)

                el == ")" -> {
                    while (st.isNotEmpty() && st.last() != "(") {
                        q2.add(st.removeLast())
                    }
                    st.removeLast()
                }

                el == "-" -> {
                    if (prevEl == "" || prevEl == "(") {
                        q2.add("0")
                    }
                    while (st.isNotEmpty() && OperationChecker.isOperation(st.last())
                        && !OperationChecker.isPriorityOperation(el, st.last())) {
                        q2.add(st.removeLast())
                    }
                    st.add(el)
                }

                OperationChecker.isOperation(el) -> {
                    while (st.isNotEmpty() && !OperationChecker.isPriorityOperation(el, st.last())) {
                        q2.add(st.removeLast())
                    }
                    st.add(el)
                }

                else -> q2.add(el)
            }
            prevEl = el
        }

        while (st.isNotEmpty()) {
            q2.add(st.removeLast())
        }

        return q2
    }

    fun calculate(expression: String): Double {
        if (!isValidExpression(expression)) {
            throw IllegalArgumentException("Выражение некорректно: неверный синтаксис")
        }

        val q: ArrayDeque<String> = translateInfixToPostfix(expression)
        val stOperands: ArrayDeque<Double> = ArrayDeque()

        while (q.isNotEmpty()) {
            val el: String = q.removeFirst()

            if (!OperationChecker.isOperation(el)) {
                stOperands.add(el.toDouble())
            } else {

                val b: Double = stOperands.removeLast()
                val a: Double = stOperands.removeLast()

                val result = when (el) {
                    "+" -> a + b
                    "-" -> a - b
                    "×" -> a * b
                    "/" -> {
                        if (b == 0.0) throw ArithmeticException("Деление на 0 недопустимо")
                        a / b
                    }

                    else -> throw IllegalArgumentException("Неизвестный оператор: $el")
                }
                if (result.isInfinite()) throw ArithmeticException("Результат является бесконечностью")
                stOperands.add(result)
            }
        }
        return stOperands.last()
    }
}
