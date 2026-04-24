package com.example.authenticationexample

import androidx.compose.runtime.Composable

//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.test.SemanticsMatcher
//import androidx.compose.ui.test.hasText
//import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.navigation.NavHostController
//import androidx.navigation.testing.TestNavHostController
//import com.example.authenticationexample.data.AuthRepo
//import com.example.authenticationexample.data.ticket.TicketRepo
//import com.example.authenticationexample.data.user.UserRole
//import com.example.authenticationexample.presentation.screens.add.AddScreenViewModel
//import dagger.hilt.android.testing.HiltAndroidRule
//import dagger.hilt.android.testing.HiltAndroidTest
//import dagger.hilt.android.testing.UninstallModules
//import org.junit.Before
//import org.junit.Rule
//import org.mockito.junit.MockitoJUnit.rule
//import jakarta.inject.Inject
//import org.mockito.kotlin.whenever
//

import android.app.Activity
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.testing.TestNavHostController
import com.example.authenticationexample.data.AuthRepo
import com.example.authenticationexample.data.ticket.TicketRepo
import com.example.authenticationexample.data.user.UserRole
import com.example.authenticationexample.navigation.NavScreen
import com.example.authenticationexample.presentation.screens.add.AddScreen
import com.example.authenticationexample.presentation.screens.add.AddScreenViewModel
import com.example.authenticationexample.presentation.screens.home.HomeScreen
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.argThat
import org.mockito.kotlin.whenever
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(AuthModule::class)
class AddScreenTests {
    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)
    @get:Rule(order = 1)
    var rule = createAndroidComposeRule<HiltTestActivity>()
    @Inject
    lateinit var authRepo: AuthRepo
    @Inject
    lateinit var ticketRepo: TicketRepo
    lateinit var navController: TestNavHostController
    lateinit var titleTextFieldMatcher: SemanticsMatcher
    lateinit var descriptionTextFieldMatcher: SemanticsMatcher
    lateinit var submitButtonMatcher: SemanticsMatcher
    private val SIGNED_IN_USER_ID = "111"
    val VALID_DESCRIPTION = "valid description"
    val VALID_TITLE = "valid title"

    @Before
    fun setUp() {
        hiltRule.inject()
        titleTextFieldMatcher = hasText(rule.activity.getString(R.string.title_hint))
        descriptionTextFieldMatcher = hasText(rule.activity.getString(R.string.description_hint))
        submitButtonMatcher = hasText(rule.activity.getString(R.string.submit_button)) and hasClickAction()
    }
    fun createMockUser(){ ///this info is not needed for all tests so call this as needed
        val mockUser = mock(FirebaseUser::class.java)
        whenever(authRepo.currentUser).thenReturn(mockUser)
        whenever(authRepo.getUserId()).thenReturn(SIGNED_IN_USER_ID)
    }
    fun setAddAsStartDestination() {
        rule.setContent {
            navController = rememberTestNavController()
            NavHost(navController, startDestination = NavScreen.ADD.route) {
                composable(NavScreen.ADD.route) {
                    AddScreen(
                        text = rule.activity.getString(R.string.add_ticket),
                        userRole = UserRole.STAFF, // only staff can view this screen so set accordingly
                        navController = navController
                    )
                }
                composable(NavScreen.HOME.route) {// needed for navigation to this screen
                    HomeScreen(
                        text = rule.activity.getString(R.string.staff_home),
                        userRole = UserRole.STAFF, // only staff can view this screen so set accordingly
                        navController = navController
                    )
                }
                composable(NavScreen.EXIT.route) {// needed for navigation to this screen
                    val context = LocalContext.current
                    finishAffinity(context as Activity)
                }
            }
        }
    }
    @Test
    fun `check bottom navigation bar for a staff user`() {
        setAddAsStartDestination()
        // Is the screen title displayed?
        rule.onNodeWithText(rule.activity.getString(R.string.add_ticket)).assertIsDisplayed()
        // Is bottom navigation bar present?
        val navItems = rule.onAllNodes(
            hasAnyAncestor(hasContentDescription("bottom navigation bar", ignoreCase = true)) and hasClickAction())
                navItems.assertCountEquals(3)
            val expectedRoutesForStaffUser = listOf(NavScreen.HOME.route, NavScreen.ADD.route,
            NavScreen.EXIT.route)
                expectedRoutesForStaffUser.forEach { route ->
            rule.onNodeWithText(route).assertIsDisplayed()
        }
    }
    @Test
    fun `check initial state of the text fields and submit button`() {
        setAddAsStartDestination()
        // Hint text is displayed for title and description fields (so text fields are empty)
        rule.onNode(titleTextFieldMatcher).assertIsDisplayed()
        rule.onNode(descriptionTextFieldMatcher).assertIsDisplayed()
        // Submit button is not enabled
        rule.onNode(submitButtonMatcher).assertIsDisplayed().assertIsNotEnabled()
        // Error messages are visible

        rule.onNodeWithText(rule.activity.getString(R.string.title_error_message)).assertIsDisplayed()

        rule.onNodeWithText(rule.activity.getString(R.string.description_error_message)).assertIsDisplayed()
    }
    @Test
    fun `adding an invalid title does not enabled the submit button`() {
        val invalidTitle = "a".repeat(4) // <5 chars as per UI state check
        setAddAsStartDestination()
        rule.onNode(titleTextFieldMatcher).performTextInput(invalidTitle)
        rule.onNode(descriptionTextFieldMatcher).performTextInput(VALID_DESCRIPTION)
        rule.onNode(submitButtonMatcher).assertIsDisplayed().assertIsNotEnabled()
    }
    @Test
    fun `adding an invalid description does not enabled the submit button`() {
        val invalidDescription = "a".repeat(9) // <10 as per UI state check
        setAddAsStartDestination()
        rule.onNode(titleTextFieldMatcher).performTextInput(VALID_TITLE)
        rule.onNode(descriptionTextFieldMatcher).performTextInput(invalidDescription)
        rule.onNode(submitButtonMatcher).assertIsDisplayed().assertIsNotEnabled()
    }
    @Test
    fun `adding valid ticket data enables the user to save the ticket`() {
        createMockUser()
        setAddAsStartDestination()
        rule.onNode(titleTextFieldMatcher).performTextInput(VALID_TITLE)
        rule.onNode(descriptionTextFieldMatcher).performTextInput(VALID_DESCRIPTION)
        // Assert button is enabled so we can click
        rule.onNode(submitButtonMatcher).performClick()
        runBlocking {
            verify(ticketRepo).insert(argThat { ticket ->
                ticket.title == VALID_TITLE &&
                        ticket.description == VALID_DESCRIPTION &&
                        ticket.createdByCustomerId == SIGNED_IN_USER_ID
            })
        }
    }
    @Test
    fun `navigate to the home screen`() {
        createMockUser()
        whenever(ticketRepo.findByUserId(SIGNED_IN_USER_ID)).thenReturn(flowOf(emptyList()))
        setAddAsStartDestination()
        rule.onNodeWithText(NavScreen.HOME.route).performClick()
        // We should now be on the home screen
        rule.onNodeWithText(rule.activity.getString(R.string.staff_home)).assertIsDisplayed()
        // You could check for other elements specific to the add screen here as well but they are
        // also checked by the add screen tests
    }
    @Test
    fun `navigate to the exit screen`() {
        setAddAsStartDestination()
        rule.onNodeWithText(NavScreen.EXIT.route).performClick()
        rule.runOnIdle {
            assertTrue(rule.activity.isFinishing)
        }
    }
}
