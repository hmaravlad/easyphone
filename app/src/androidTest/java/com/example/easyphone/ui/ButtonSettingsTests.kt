package com.example.easyphone.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.easyphone.R
import com.example.easyphone.ui.button.settings.ButtonSettingsFragment
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class ButtonSettingsTests {
    private lateinit var stringToBetyped: String
    private var invalidHeight: Int = 16
    private var invalidWidth: Int = 16
    private val color = "Red"
    private val colorNum = Color.parseColor("#b71c1c")

    private lateinit var scenario: FragmentScenario<ButtonSettingsFragment>



    @Before
    fun initData() {
        // Specify a valid string.
        stringToBetyped = "Test"
    }

    @Before
    fun initScenario() {
        val fragmentArgs = bundleOf("buttonId" to -1)
        scenario = launchFragmentInContainer<ButtonSettingsFragment>(fragmentArgs)
    }

    @Test
    fun changeButtonText() {
        Espresso.onView(ViewMatchers.withId(R.id.buttonNameField))
            .perform(ViewActions.typeText(stringToBetyped), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.buttonPreview))
            .check(ViewAssertions.matches(ViewMatchers.withText(stringToBetyped)))
    }

    @Test
    fun changeButtonHeightTooBig() {
        Espresso.onView(ViewMatchers.withId(R.id.buttonHeightField))
            .perform(
                ViewActions.typeText(invalidHeight.toString()),
                ViewActions.closeSoftKeyboard()
            )

        var error = ""


        scenario.onFragment(object : FragmentScenario.FragmentAction<ButtonSettingsFragment> {
            override fun perform(fragment: ButtonSettingsFragment) {
                error = fragment.requireContext().resources.getString(R.string.row_error_big)
            }
        })


        Espresso.onView(ViewMatchers.withId(R.id.buttonHeightField))
            .check(ViewAssertions.matches(withError(error)))
    }

    @Test
    fun changeButtonWidthTooBig() {
        Espresso.onView(ViewMatchers.withId(R.id.buttonLengthField))
            .perform(
                ViewActions.typeText(invalidWidth.toString()),
                ViewActions.closeSoftKeyboard()
            )

        var error = ""


        scenario.onFragment(object : FragmentScenario.FragmentAction<ButtonSettingsFragment> {
            override fun perform(fragment: ButtonSettingsFragment) {
                error = fragment.requireContext().resources.getString(R.string.column_error_big)
            }
        })


        Espresso.onView(ViewMatchers.withId(R.id.buttonLengthField))
            .check(ViewAssertions.matches(withError(error)))
    }

    @Test
    fun changeButtonHeight0() {
        Espresso.onView(ViewMatchers.withId(R.id.buttonHeightField))
            .perform(
                ViewActions.typeText("0"),
                ViewActions.closeSoftKeyboard()
            )

        var error = ""


        scenario.onFragment(object : FragmentScenario.FragmentAction<ButtonSettingsFragment> {
            override fun perform(fragment: ButtonSettingsFragment) {
                error = fragment.requireContext().resources.getString(R.string.row_error_0)
            }
        })


        Espresso.onView(ViewMatchers.withId(R.id.buttonHeightField))
            .check(ViewAssertions.matches(withError(error)))
    }

    @Test
    fun changeButtonWidth0() {
        Espresso.onView(ViewMatchers.withId(R.id.buttonLengthField))
            .perform(
                ViewActions.typeText("0"),
                ViewActions.closeSoftKeyboard()
            )

        var error = ""


        scenario.onFragment(object : FragmentScenario.FragmentAction<ButtonSettingsFragment> {
            override fun perform(fragment: ButtonSettingsFragment) {
                error = fragment.requireContext().resources.getString(R.string.column_error_0)
            }
        })


        Espresso.onView(ViewMatchers.withId(R.id.buttonLengthField))
            .check(ViewAssertions.matches(withError(error)))
    }


    @Test
    fun changeButtonColor() {

        Espresso.onView(ViewMatchers.withId(R.id.colorField))
            .perform(
                ViewActions.click()
            )
        Espresso.onView(ViewMatchers.withText(color))
            .perform(
                ViewActions.click()
            )


        Espresso.onView(ViewMatchers.withId(R.id.buttonPreview))
            .check(ViewAssertions.matches(withColor(colorNum)))


    }
}

fun withError(error: String): org.hamcrest.Matcher<View?>? {
    androidx.test.internal.util.Checks.checkNotNull(error)
    return object : BoundedMatcher<View?, EditText>(EditText::class.java) {
        override fun matchesSafely(warning: EditText): Boolean {
            Log.d("TESt_DEBUG", "the error $error")
            Log.d("TESt_DEBUG", "the warning.error ${warning.error}")

            println(error)
            println(warning.error)
            return error == warning.error
        }

        override fun describeTo(description: org.hamcrest.Description) {
            description.appendText("with error: ${error}")
        }
    }
}

fun withColor(color: Int): org.hamcrest.Matcher<View?>? {
    androidx.test.internal.util.Checks.checkNotNull(color)
    return object : BoundedMatcher<View?, Button>(Button::class.java) {
        override fun matchesSafely(button: Button): Boolean {

            var buttonColor: Int = Color.TRANSPARENT
            val background: Drawable = button.getBackground()
            if (background is ColorDrawable) buttonColor = background.color
            return buttonColor == color
        }

        override fun describeTo(description: org.hamcrest.Description) {
            description.appendText("with color: ${color}")
        }
    }
}