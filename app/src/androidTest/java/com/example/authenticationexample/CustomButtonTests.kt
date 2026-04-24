package com.example.authenticationexample

import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.authenticationexample.presentation.components.CustomButton
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CustomButtonTest {
    private val TEXT_DISPLAY = "button text"
    private val buttonMatcher = hasContentDescription("$TEXT_DISPLAY button")  and
            hasClickAction()
    @get:Rule
    var rule = createComposeRule()
    @Test
    fun button_displays_text_is_enabled_by_default_and_executes_function() {
        var sampleStateToTest: Boolean = false
        rule.setContent {
            CustomButton(
                text = TEXT_DISPLAY,
                clickButton = { sampleStateToTest = true }
            )
        }
        rule.onNode(buttonMatcher).assertExists()
            .performClick()
        assertTrue(sampleStateToTest)
    }
    @Test
    fun button_is_not_enabled_when_enabled_is_set_to_false() {
        rule.setContent {
            CustomButton(
                text = TEXT_DISPLAY,
                clickButton = {},
                enabled = false
            )
        }
        rule.onNode(buttonMatcher).assertExists()
            .assertIsNotEnabled()
    }
}