package com.example.authenticationexample

import androidx.activity.ComponentActivity
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.test.runner.AndroidJUnitRunner
import com.example.authenticationexample.presentation.components.CustomTextField
import dagger.hilt.android.AndroidEntryPoint
import junit.framework.TestCase.assertEquals
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runners.MethodSorters

@FixMethodOrder( MethodSorters.DEFAULT)
class CustomTextFieldTest {
    private val HINT_TEXT = "hint text"
    private val TEXT_TO_BE_DISPLAYED = "text"
    private var textForInput = ""
    private val ERROR_MESSAGE_TEXT = "error displayed"
    @get:Rule
    var rule = createComposeRule()
    @Test
    fun `check default state of the text field with text present`() {
        rule.setContent {
            CustomTextField(
                hintText = HINT_TEXT,
                text = TEXT_TO_BE_DISPLAYED,
                isPasswordField = false,
                onValueChange = { textForInput = it },
                errorMessage = ERROR_MESSAGE_TEXT,
                errorPresent = false
            )
        }
        rule.onNodeWithText(HINT_TEXT).assertIsDisplayed()
        rule.onNodeWithText(TEXT_TO_BE_DISPLAYED).assertIsDisplayed()
        rule.onNodeWithText(ERROR_MESSAGE_TEXT).assertIsNotDisplayed()
    }

    @Test
    fun `check state of the text field when additional text is added`() {
        val additionalTextForInput = "something"
        rule.setContent {
            CustomTextField(
                hintText = "",
                text = TEXT_TO_BE_DISPLAYED,
                isPasswordField = false,
                onValueChange = { textForInput = it },
                errorMessage = "",
                errorPresent = false
            )
        }
        rule.onNodeWithText(TEXT_TO_BE_DISPLAYED).performTextInput(additionalTextForInput)
        rule.onNodeWithText(ERROR_MESSAGE_TEXT).assertIsNotDisplayed()
        //Cursor is not at the end and setting the mouse cursor as such requires modification to onValueChange
        assertEquals(textForInput, additionalTextForInput.plus(TEXT_TO_BE_DISPLAYED))
    }
    @Test
    fun `when errorPresent is true an error message is displayed`() {
        //As validation of the button is external and this component is tested separately
        //we need to set the errorPresent to true explicitly
        rule.setContent {
            CustomTextField(
                hintText = HINT_TEXT,
                text = "",
                isPasswordField = false,
                onValueChange = { },
                errorMessage = ERROR_MESSAGE_TEXT,
                errorPresent = true
            )
        }
        rule.onNodeWithText(ERROR_MESSAGE_TEXT).assertIsDisplayed()
        //Generic check but ensures that the error flag is active for accessibility reasons
        rule.onNodeWithText(HINT_TEXT).assert(
            SemanticsMatcher.keyIsDefined(SemanticsProperties.Error)
        )
    }

    @Test
    fun `text field does not display plain text when isPasswordField is true`() {
        val passwordText = "my password"
        val expectedMask = "*".repeat(passwordText.length)
        rule.setContent {
            CustomTextField(
                hintText = HINT_TEXT,
                text = passwordText,
                isPasswordField = true, //mask password
                onValueChange = { },
                errorMessage = ERROR_MESSAGE_TEXT,
                errorPresent = false
            )
        }
        //inputText property still has the plain text password - so looking for passwordText
        //would find the plain text password
        //rule.onNodeWithText(passwordText).assertExists()
        rule.onNodeWithText(HINT_TEXT).assert(hasText(expectedMask))
    }
}
