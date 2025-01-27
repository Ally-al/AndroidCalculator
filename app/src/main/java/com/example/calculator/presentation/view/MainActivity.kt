package com.example.calculator.presentation.view

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.calculator.R
import com.example.calculator.presentation.presenter.CalculatorContract
import com.example.calculator.presentation.presenter.CalculatorPresenter

class MainActivity : AppCompatActivity(), CalculatorContract.View {

    private lateinit var presenter: CalculatorContract.Presenter
    private lateinit var textViewResult: EditText
    private lateinit var scrollView: ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        scrollView = findViewById(R.id.scrollView)
        textViewResult = findViewById(R.id.result)

        presenter = CalculatorPresenter()
        presenter.attachView(this)

        setupDigitButtonListeners()
        setupOperatorButtonListeners()
        setupPointButtonListener()
        setupParenthesisButtonListeners()

        val buttonEquals: Button = findViewById(R.id.bEqual)
        buttonEquals.setOnClickListener {
            animateButton(it as Button)
            val expression: String = textViewResult.text.toString()
            presenter.onCalculate(expression)
        }

        val buttonClear: Button = findViewById(R.id.bClear)
        buttonClear.setOnClickListener {
            animateButton(it as Button)
            presenter.onClear()
        }
        buttonClear.setOnLongClickListener {
            animateButton(it as Button)
            presenter.onClearAll()
            true
        }

    }

    override fun showResult(result: String) {
        textViewResult.setText(result)
        scrollToBottom()
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun clearResult() {
        val updatedText = textViewResult.text.dropLast(1)
        textViewResult.setText(updatedText)
        presenter.updateCurrentExpression(updatedText.toString())
    }

    override fun clearAllResult() {
        textViewResult.setText("")
        presenter.updateCurrentExpression("")
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    private fun setupDigitButtonListeners() {
        val digitButtons = listOf(
            R.id.b0 to "0",
            R.id.b1 to "1",
            R.id.b2 to "2",
            R.id.b3 to "3",
            R.id.b4 to "4",
            R.id.b5 to "5",
            R.id.b6 to "6",
            R.id.b7 to "7",
            R.id.b8 to "8",
            R.id.b9 to "9"
        )

        digitButtons.forEach { (id, digit) ->
            findViewById<Button>(id).setOnClickListener {
                animateButton(it as Button)
                presenter.onDigitPressed(digit)
            }
        }
    }

    private fun setupOperatorButtonListeners() {
        val operatorButtons = listOf(
            R.id.bAdd to "+",
            R.id.bSubtract to "-",
            R.id.bMultiply to "×",
            R.id.bDivide to "/"
        )

        operatorButtons.forEach { (id, operator) ->
            findViewById<Button>(id).setOnClickListener {
                animateButton(it as Button)
                presenter.onOperatorPressed(operator)
            }
        }
    }

    private fun setupPointButtonListener() {
        findViewById<Button>(R.id.bPoint).setOnClickListener {
            animateButton(it as Button)
            presenter.onPointPressed(".")
        }
    }

    private fun setupParenthesisButtonListeners() {
        val parenthesisButtons = listOf(
            R.id.bLParenthesis to "(",
            R.id.bRParenthesis to ")",
        )

        parenthesisButtons.forEach { (id, parenthesis) ->
            findViewById<Button>(id).setOnClickListener {
                animateButton(it as Button)
                presenter.onParenthesisPressed(parenthesis)
            }
        }
    }

    private fun scrollToBottom() {
        scrollView.post {
            scrollView.fullScroll(View.FOCUS_DOWN)
        }
    }

    private fun animateButton(button: Button) {
        val scaleDown = AnimationUtils.loadAnimation(this, R.anim.scale_down)
        val scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up)

        scaleDown.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                button.startAnimation(scaleUp)
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        })

        button.startAnimation(scaleDown)
    }
}
