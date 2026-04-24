package com.example.authenticationexample

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
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
import com.example.authenticationexample.data.ticket.TicketRepo
import com.example.authenticationexample.data.user.UserRepo
import com.example.authenticationexample.data.user.UserRole
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.flow.flowOf
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
class LoginScreenTests {
    val VALID_PASSWORD = "passwordpassword"
    lateinit var emailAddressTextFieldMatcher: SemanticsMatcher
    lateinit var passwordTextFieldMatcher: SemanticsMatcher
    lateinit var submitButtonMatcher: SemanticsMatcher
    lateinit var forgotPasswordButtonMatcher: SemanticsMatcher
    lateinit var signUpButtonMatcher: SemanticsMatcher

    @Inject
    lateinit var authRepo: AuthRepo
    @Inject
    lateinit var userRepo: UserRepo
    @Inject
    lateinit var ticketRepo: TicketRepo //destination from login screen retrieves tickets for the user

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)
    @get:Rule(order = 1)
    var rule = createAndroidComposeRule<MainActivity>()
    @Before
    fun setUp() {
        hiltRule.inject()
        emailAddressTextFieldMatcher = hasText(rule.activity.getString(R.string.email))
        passwordTextFieldMatcher = hasText(rule.activity.getString(R.string.password))
        submitButtonMatcher = hasText(rule.activity.getString(R.string.submit_button)) and
                hasClickAction()
        forgotPasswordButtonMatcher = hasText( rule.activity.getString(R.string.forgot_password)) and hasClickAction()
        signUpButtonMatcher = hasText(rule.activity.getString(R.string.sign_up_button)) and
                hasClickAction()
    }
    fun performFailedLoginTest(email: String,
                               password: String
    ){
        val exception = Exception("")//set the message to ""
        runBlocking {
            whenever(authRepo.signInWithEmailAndPassword(email, password))
                .thenReturn(Response.Failure(exception))
        }
    }
    fun performSuccessfulLogin(uid: String,
                               email: String,
                               role: UserRole
    ) {
        val mockUser = mock(FirebaseUser::class.java)
        whenever(mockUser.uid).thenReturn(uid)
        whenever(mockUser.isEmailVerified).thenReturn(true)
        whenever(authRepo.currentUser).thenReturn(mockUser)
        whenever(authRepo.getUserId()).thenReturn(uid)
        //actions have suspending function so use runBlocking
        runBlocking {
            whenever(authRepo.signInWithEmailAndPassword(email, VALID_PASSWORD))
                .thenReturn(Response.Success)
            whenever(userRepo.getUserRole(uid)).thenReturn(role)
        }
        //Mock the Ticket Repo to avoid a null pointer exception when the destination vm requires a flow
        whenever(ticketRepo.findByUserId(uid)).thenReturn(flowOf(emptyList())) //staff
        whenever(ticketRepo.findAll()).thenReturn(flowOf(emptyList())) //manager
        rule.onNode(emailAddressTextFieldMatcher).performTextInput(email)
        rule.onNode(passwordTextFieldMatcher).performTextInput(VALID_PASSWORD)
        rule.onNode(submitButtonMatcher).performClick()
    }

    @Test
    fun `check the initial state of the login in page`() {
        val loginPage = hasContentDescription(rule.activity.getString(R.string.login_screen))
        rule.onNode(loginPage).assertExists()
        rule.onNode(submitButtonMatcher).assertExists()
        rule.onNode(forgotPasswordButtonMatcher).assertExists()
        rule.onNode(signUpButtonMatcher).assertExists()
        rule.onNode(emailAddressTextFieldMatcher).assertExists()
        rule.onNode(passwordTextFieldMatcher).assertExists()
    }

    @Test
    fun `check if navigated to the sign up page when sign up button clicked`(){
        rule.onNode(signUpButtonMatcher).performClick()
        val signUpPage = hasContentDescription(rule.activity.getString(R.string.sign_up_screen))
        rule.onNode(signUpPage).assertExists()
    }
    @Test
    fun `providing incorrect email causes validation to be displayed`(){
        val invalidEmail = "test"
        rule.onNode(emailAddressTextFieldMatcher).performTextInput(invalidEmail)
        rule.onNode(passwordTextFieldMatcher).performTextInput(VALID_PASSWORD)
        rule.onNodeWithText(rule.activity.getString((R.string.email_error_message))).assertIsDisplayed(
        )
        rule.onNode(submitButtonMatcher).assertIsDisplayed().assertIsNotEnabled()
    }
//Add test for incorrect password as well - same idea as above
    @Test
    fun `providing a valid email and password that are not registered displays an error message`(){
        val validButUnknownEmail = "someone@email.com"
        val errorMessage = "Unable to sign in:"
        performFailedLoginTest(validButUnknownEmail, VALID_PASSWORD);
        rule.onNode(emailAddressTextFieldMatcher).performTextInput(validButUnknownEmail)
        rule.onNode(passwordTextFieldMatcher).performTextInput(VALID_PASSWORD)
        rule.onNode(submitButtonMatcher).performClick()
        rule.waitUntil(1000) {
        rule.onAllNodesWithText(errorMessage, substring = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        rule.onNodeWithText(errorMessage, substring = true).assertIsDisplayed()
        //rule.onRoot().printToLog("DEBUG")
    }
    @Test
    fun `destination is the staff home page when correct staff email and password are entered`() {
        performSuccessfulLogin(
            uid = "111",
            email = "staff@example.com",
            role = UserRole.STAFF
        )
        rule.onNodeWithText(rule.activity.getString( R.string.staff_home)).assertIsDisplayed()
    }
    @Test
    fun `destination is the manager home page when correct manager email and password are entered`() {
        performSuccessfulLogin(
            uid = "222",
            email = "manager@example.com",
            role = UserRole.MANAGER
        )
        rule.onNodeWithText(rule.activity.getString(  R.string.manager_home)).assertIsDisplayed()
    }
}



