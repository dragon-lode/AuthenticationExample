package com.example.authenticationexample

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.authenticationexample.data.AuthRepo
import com.example.authenticationexample.data.Response
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runners.MethodSorters
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(AuthModule::class)
@FixMethodOrder( MethodSorters.DEFAULT)
class SignUpScreenTests {
    val VALID_FIRSTNAME = "Phil"
    val VALID_SURNAME = "James"
    val VALID_EMAIL = "newuser@email.com"
    val VALID_PASSWORD = "passwordpassword"

    lateinit var signUpButtonMatcher: SemanticsMatcher
    lateinit var firstNameMatcher: SemanticsMatcher
    lateinit var surnameMatcher: SemanticsMatcher
    lateinit var emailAddressTextFieldMatcher: SemanticsMatcher
    lateinit var passwordTextFieldMatcher: SemanticsMatcher
    lateinit var submitButtonMatcher: SemanticsMatcher
    val bottomNavBar = hasContentDescription("bottom navigation") //make a string
    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)
    @get:Rule(order = 1)
    var rule = createAndroidComposeRule<MainActivity>()
    @Inject
    lateinit var authRepo: AuthRepo
    @Before
    fun setUp() {
        hiltRule.inject()
        submitButtonMatcher = hasText(rule.activity.getString(R.string.submit_button)) and
                hasClickAction()
        signUpButtonMatcher = hasText(rule.activity.getString(R.string.sign_up_button)) and
                hasClickAction()
        firstNameMatcher = hasText(rule.activity.getString(R.string.first_name_hint))
        surnameMatcher = hasText(rule.activity.getString(R.string.surname_hint))
        emailAddressTextFieldMatcher = hasText(rule.activity.getString(R.string.email))
        passwordTextFieldMatcher = hasText(rule.activity.getString(R.string.password))
    }
    @Test
    fun `check default state of the sign up screen`() {
        rule.onNode(signUpButtonMatcher).performClick()
        //on sign up page
        val signUpPage = hasContentDescription(rule.activity.getString(R.string.sign_up_screen))
        rule.onNode(signUpPage).assertExists()
        rule.onNode(bottomNavBar).assertDoesNotExist()
        rule.onNode(emailAddressTextFieldMatcher).assertExists()
        rule.onNode(passwordTextFieldMatcher).assertExists()
        rule.onNode(submitButtonMatcher).assertExists()
    }
    @Test
    fun `submit invalid email but valid password`() {
        val blankEmail = ""
        rule.onNode(signUpButtonMatcher).performClick()
        rule.onNode(emailAddressTextFieldMatcher).performTextInput(blankEmail)
        rule.onNode(passwordTextFieldMatcher).performTextInput(VALID_PASSWORD)
        rule.onNode(submitButtonMatcher).performClick()
    }
    @Test
    fun `submit valid email but invalid password`() {
        val blankPassword = ""
        rule.onNode(signUpButtonMatcher).performClick()
        rule.onNode(emailAddressTextFieldMatcher).performTextInput(VALID_EMAIL)
        rule.onNode(passwordTextFieldMatcher).performTextInput(blankPassword)
        rule.onNode(submitButtonMatcher).performClick()
    }

    @Test
    fun `enter valid sign up details`(){
        val uid = "111"
        val mockUser = mock(FirebaseUser::class.java)
        whenever(mockUser.uid).thenReturn(uid)
        whenever(authRepo.getUserId()).thenReturn(uid)

        runBlocking {
            whenever(authRepo.signUpWithEmailAndPassword(VALID_EMAIL, VALID_PASSWORD))
                .thenReturn(Response.NotConfirmed)
            whenever(authRepo.sendEmailVerification())
                .thenReturn(Response.Success)
            //no need to do this:
            //whenever(userRepo.createUserProfile(validUser)).thenReturn(Response.Success)
        }

        val confirmationMessage =  "Confirm details via email"
        rule.onNode(signUpButtonMatcher).performClick()
        rule.onNode(firstNameMatcher).performTextInput(VALID_FIRSTNAME)
        rule.onNode(surnameMatcher).performTextInput(VALID_SURNAME)
        rule.onNode(emailAddressTextFieldMatcher).performTextInput(VALID_EMAIL)
        rule.onNode(passwordTextFieldMatcher).performTextInput(VALID_PASSWORD)
        rule.onNode(submitButtonMatcher).performClick()
        //Should display confirmation (pause to allow display)
        rule.waitUntil(1000) {
            rule.onAllNodesWithText(confirmationMessage, substring = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        rule.onNodeWithText(confirmationMessage, substring = true).assertIsDisplayed()
        //Saves user details to firestore but we are not testing that...
    }
}