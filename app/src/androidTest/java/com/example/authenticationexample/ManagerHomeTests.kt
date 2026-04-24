package com.example.authenticationexample

import android.app.Activity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.testing.TestNavHostController
import com.example.authenticationexample.data.AuthRepo
import com.example.authenticationexample.data.ticket.Ticket
import com.example.authenticationexample.data.ticket.TicketRepo
import com.example.authenticationexample.data.user.UserRole
import com.example.authenticationexample.navigation.NavScreen
import com.example.authenticationexample.presentation.screens.home.BackgroundColourKey
import com.example.authenticationexample.presentation.screens.managerEditTicket.ManagerEditTicket
import com.example.authenticationexample.presentation.screens.managerHome.ManagerHomeScreen
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import java.util.Date
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(AuthModule::class)
class ManagerHomeTests {
    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)
    @get:Rule(order = 1)
    var rule = createAndroidComposeRule<HiltTestActivity>()
    @Inject
    lateinit var authRepo: AuthRepo
    @Inject
    lateinit var ticketRepo: TicketRepo
    lateinit var navController: TestNavHostController
    private val SIGNED_IN_USER_ID = "111"

    lateinit var viewTicketButtonMatcher: SemanticsMatcher
    lateinit var deleteTicketButtonMatcher: SemanticsMatcher
    lateinit var ticketToSelectMatcher: SemanticsMatcher

    @Before
    fun setUp() {
        hiltRule.inject()
        viewTicketButtonMatcher = hasText(rule.activity.getString(R.string.view_ticket_button)) and
                hasClickAction()
        deleteTicketButtonMatcher = hasText(rule.activity.getString(R.string.delete_ticket_button)) and
                hasClickAction()
        ticketToSelectMatcher = hasText(testTicket.toString())
    }
    private val testTicket = Ticket(
        title = "Test Ticket",
        createdByCustomerId = SIGNED_IN_USER_ID,
        updatedAt = Date()
    )
    fun setManagerHomeAsStartDestination(ticketsFlow: List<Ticket> = emptyList()) {
        setManagerHomeAsStartDestination(flowOf(ticketsFlow))
    }
    fun setManagerHomeAsStartDestination(ticketsFlow: Flow<List<Ticket>>) {
        whenever(ticketRepo.findAll()).thenReturn(ticketsFlow) // for manager home screen to display tickets
        rule.setContent {
            navController = rememberTestNavController()
            NavHost(navController, startDestination = NavScreen.MANAGER_HOME.route) {
                composable(NavScreen.MANAGER_HOME.route) {
                    ManagerHomeScreen(
                        text = rule.activity.getString(R.string.manager_home),
                        userRole = UserRole.MANAGER, // make sure to get this right!
                        navController = navController,
                        selectTicketFromList = { testTicket },
                        onClickGoToEditTicketScreen = {
                            navController.navigate(NavScreen.MANAGER_EDIT_TICKET.route) }
                    )
                }
                composable(NavScreen.MANAGER_EDIT_TICKET.route) { // needed for navigation to this screen
                    ManagerEditTicket(
                        titleText = rule.activity.getString(R.string.manager_edit_ticket),
                        selectedTicket = testTicket,
                        returnToHomeScreen = {
                            navController.popBackStack()
                        },
                        userRole = UserRole.MANAGER, // make sure to get this right!
                        navController = navController
                    )
                }
                composable(NavScreen.EXIT.route) { // needed for navigation to this screen
                    val context = LocalContext.current
                    finishAffinity(context as Activity)
                }
            }
        }
    }
    @Test
    fun `check bottom navigation bar and default display for a manager`() {
        setManagerHomeAsStartDestination()

        // Is the screen title displayed?
        rule.onNodeWithText(rule.activity.getString( R.string.manager_home)).assertIsDisplayed()
        // Is bottom navigation bar present and does it have the correct options for a manager?
        rule.onNode(hasContentDescription("bottom navigation bar")).assertIsDisplayed()
        val navItems = rule.onAllNodes(hasAnyAncestor(hasContentDescription("bottom navigation bar"))
                and hasClickAction())
        navItems.assertCountEquals(2)

        val expectedRoutesForManager = listOf(NavScreen.MANAGER_HOME.route, NavScreen.EXIT.route)
        expectedRoutesForManager.forEach { route ->
            rule.onNodeWithText(route).assertIsDisplayed()
        }
        // Are the buttons present but disabled since no ticket is selected?
        rule.onNode(viewTicketButtonMatcher).assertIsDisplayed().assertIsNotEnabled()
        rule.onNode(deleteTicketButtonMatcher).assertIsDisplayed().assertIsNotEnabled()
    }



    @Test
    fun `if a ticket is selected then the view and delete buttons are enabled`() {
        setManagerHomeAsStartDestination(listOf(testTicket))
        // Is the selected ticket displayed - if so click it
        rule.onNode(ticketToSelectMatcher).performClick()
        // Is the selected ticket highlighted? (background colour should be black)
        rule.onNode(ticketToSelectMatcher).assert(
            SemanticsMatcher.expectValue(BackgroundColourKey, Color.Black)
        )
        // Are the buttons enabled?
        rule.onNode(viewTicketButtonMatcher).assertIsDisplayed().assertIsEnabled()
        rule.onNode(deleteTicketButtonMatcher).assertIsDisplayed().assertIsEnabled()
    }
    @Test
    fun `clicking delete on a selected ticket displays an Alert dialog`() {
        setManagerHomeAsStartDestination(listOf(testTicket))
        rule.onNode(ticketToSelectMatcher).performClick()
        rule.onNode(deleteTicketButtonMatcher).performClick()
        // Alert dialog should now be displayed with the correct title, message and buttons
        rule.onNodeWithText(rule
            .activity.getString(R.string.confirm_deletion_dialog_heading))
            .assertIsDisplayed()

        rule.onNodeWithText(rule
            .activity.getString(R.string.confirm_deletion_dialog_message))
            .assertIsDisplayed()

        rule.onNodeWithText(rule
            .activity.getString(R.string.confirm_deletion_button))
            .assertIsDisplayed()
            .assertIsEnabled() // added this text to strings.xml

        rule.onNodeWithText(rule
            .activity.getString(R.string.cancel_deletion_button))
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    @Test
    fun `cancelling the dialog after selecting a ticket removes the dialog`() {
        setManagerHomeAsStartDestination(listOf(testTicket))
        rule.onNode(ticketToSelectMatcher).performClick()
        rule.onNode(deleteTicketButtonMatcher).performClick()
        rule.onNodeWithText(rule.activity.getString(R.string.cancel_deletion_button)).performClick()
        rule.onNode(isDialog()).assertDoesNotExist()
    }
    @Test
    fun `confirming a deletion removes the ticket and the dialog`() {
        val ticketFlow = MutableStateFlow(listOf(testTicket))
        setManagerHomeAsStartDestination(ticketFlow)
        // we could verify delete is called but it not be a test of the UI
        rule.onNode(ticketToSelectMatcher).performClick()
        rule.onNode(deleteTicketButtonMatcher).performClick()
        rule.onNodeWithText(rule.activity.getString(R.string.confirm_deletion_button)).performClick()

        ticketFlow.value = emptyList() // modify the flow which should be reflected in the interface
        rule.onNode(isDialog()).assertDoesNotExist()
        // the ticket should no longer be displayed
        rule.onNode(ticketToSelectMatcher).assertDoesNotExist()
    }
    @Test
    fun `navigate to the exit screen`() {
        setManagerHomeAsStartDestination()
        rule.onNodeWithText(NavScreen.EXIT.route).performClick()
        rule.runOnIdle {
            assertTrue(rule.activity.isFinishing || rule.activity.isDestroyed)
        }
    }
    @Test
    fun `navigate to the manager edit screen`() {
        setManagerHomeAsStartDestination(listOf(testTicket))
        // mocks required for the destination vm screen to load without error when we navigate to it
        val mockUser = mock(FirebaseUser::class.java)
        whenever(authRepo.currentUser).thenReturn(mockUser)
        whenever(authRepo.getUserId()).thenReturn(SIGNED_IN_USER_ID)
        runBlocking { // required dependency action for destination vm to load without error
            whenever(ticketRepo.getAllNotesForTicket(testTicket.uid)).thenReturn(emptyList())
        }

        // Select a ticket
        rule.onNode(ticketToSelectMatcher).assertIsDisplayed().performClick()
        rule.onNode(viewTicketButtonMatcher).performClick()
        // We should now be on the manager edit screen
        rule.onNodeWithText(rule.activity.getString(R.string.manager_edit_ticket)).assertIsDisplayed()
        // You could check for other elements specific to the edit screen here as well
    }
}