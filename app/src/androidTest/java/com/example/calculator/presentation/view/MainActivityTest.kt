package com.example.calculator.presentation.view

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.calculator.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    // on button pressed
    @Test
    fun onDigitPressedAddsDigitToExpressionAndUpdatesView() {
        // "1"
        onView(withId(R.id.b1)).perform(click())
        onView(withId(R.id.result)).check(matches(withText("1")))
    }

    @Test
    fun onOperatorPressedAddsOperatorIfValidAndUpdatesView() {
        // "1+"
        onView(withId(R.id.b1)).perform(click())
        onView(withId(R.id.bAdd)).perform(click())
        onView(withId(R.id.result)).check(matches(withText("1+")))
    }

    @Test
    fun onOperatorPressedDoesNotAddOperatorIfInvalid() {
        // "+"
        onView(withId(R.id.bAdd)).perform(click())
        onView(withId(R.id.result)).check(matches(withText("")))
    }

    @Test
    fun onPointPressedAddsPointIfValidAndUpdatesView() {
        // "1."
        onView(withId(R.id.b1)).perform(click())
        onView(withId(R.id.bPoint)).perform(click())
        onView(withId(R.id.result)).check(matches(withText("1.")))
    }

    @Test
    fun onPointPressedDoesNotAddPointIfInvalid() {
        // "1.."
        onView(withId(R.id.b1)).perform(click())
        onView(withId(R.id.bPoint)).perform(click())
        onView(withId(R.id.bPoint)).perform(click())
        onView(withId(R.id.result)).check(matches(withText("1.")))
    }

    @Test
    fun onParenthesisPressedAddsParenthesisIfValidAndUpdatesView() {
        // "("
        onView(withId(R.id.bLParenthesis)).perform(click())
        onView(withId(R.id.result)).check(matches(withText("(")))
    }

    @Test
    fun onParenthesisPressedDoesNotAddClosingParenthesisIfInvalid() {
        // ")"
        onView(withId(R.id.bRParenthesis)).perform(click())
        onView(withId(R.id.result)).check(matches(withText("")))
    }

    // on calculate
    @Test
    fun onCalculateSimpleExpression() {
        // "1+3="
        onView(withId(R.id.b1)).perform(click())
        onView(withId(R.id.bAdd)).perform(click())
        onView(withId(R.id.b3)).perform(click())
        onView(withId(R.id.bEqual)).perform(click())
        onView(withId(R.id.result)).check(matches(withText("4")))
    }

    @Test
    fun onCalculateComputesExpressionWithMultipleDigitsAndDecimals() {
        // "1.444444444*0.2="
        onView(withId(R.id.b1)).perform(click())
        onView(withId(R.id.bPoint)).perform(click())
        repeat(9) {
            onView(withId(R.id.b4)).perform(click())
        }
        onView(withId(R.id.bMultiply)).perform(click())
        onView(withId(R.id.b0)).perform(click())
        onView(withId(R.id.bPoint)).perform(click())
        onView(withId(R.id.b2)).perform(click())
        onView(withId(R.id.bEqual)).perform(click())
        onView(withId(R.id.result)).check(matches(withText("0.2888888888")))
    }

    // on clear
    @Test
    fun onClearUpdatesViewAndDoesNotAffectFullExpression() {
        // "1+3C"
        onView(withId(R.id.b1)).perform(click())
        onView(withId(R.id.bAdd)).perform(click())
        onView(withId(R.id.b3)).perform(click())
        onView(withId(R.id.bClear)).perform(click())
        onView(withId(R.id.result)).check(matches(withText("1+")))
    }

    @Test
    fun onClearAllClearsEntireExpressionAndUpdatesView() {
        // "1+2"
        onView(withId(R.id.b1)).perform(click())
        onView(withId(R.id.bAdd)).perform(click())
        onView(withId(R.id.b2)).perform(click())
        onView(withId(R.id.bClear)).perform(longClick())
        onView(withId(R.id.result)).check(matches(withText("")))
    }

    // format result
    @Test
    fun formatResultShouldFormatResultCorrectlyForWholeNumbers() {
        // "1+5.0="
        onView(withId(R.id.b1)).perform(click())
        onView(withId(R.id.bAdd)).perform(click())
        onView(withId(R.id.b5)).perform(click())
        onView(withId(R.id.bPoint)).perform(click())
        onView(withId(R.id.b0)).perform(click())
        onView(withId(R.id.bEqual)).perform(click())
        onView(withId(R.id.result)).check(matches(withText("6")))
    }

    @Test
    fun formatResultShouldNotFormatResultForShortDecimalNumbers() {
        // "1.2="
        onView(withId(R.id.b1)).perform(click())
        onView(withId(R.id.bPoint)).perform(click())
        onView(withId(R.id.b2)).perform(click())
        onView(withId(R.id.bEqual)).perform(click())
        onView(withId(R.id.result)).check(matches(withText("1.2")))
    }
}
