package com.example.authenticationexample

import android.app.Activity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createAndroidComposeRule
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
import com.example.authenticationexample.presentation.screens.add.AddScreen
import com.example.authenticationexample.presentation.screens.home.HomeScreen
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import jakarta.inject.Inject
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import java.util.Date

@HiltAndroidTest
@UninstallModules(AuthModule::class)
class HomeScreenTests {
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
    @Before
    fun setUp() {
        hiltRule.inject()
    }
    fun setHomeAsStartDestination(listOfTickets: List<Ticket> = emptyList()) { // pass, or provide empty list
        val mockUser = mock(FirebaseUser::class.java)
        whenever(authRepo.currentUser).thenReturn(mockUser)
        whenever(authRepo.getUserId()).thenReturn(SIGNED_IN_USER_ID)
        whenever(ticketRepo.findByUserId(SIGNED_IN_USER_ID)).thenReturn(flowOf(listOfTickets))

        rule.setContent {
            navController = rememberTestNavController()
            NavHost(navController, startDestination = NavScreen.HOME.route) {
                composable(NavScreen.HOME.route) {
                    HomeScreen(
                        text = rule.activity.getString(R.string.staff_home),
                        userRole = UserRole.STAFF, // only staff can view this screen so set accordingly
                        navController = navController
                    )
                }
                composable(NavScreen.ADD.route) { // needed for test for navigation to this screen
                    AddScreen(
                        text = rule.activity.getString(R.string.add_ticket),
                        userRole = UserRole.STAFF, // only staff can view this screen so set accordingly
                        navController = navController
                    )
                }
                composable(NavScreen.EXIT.route) {// needed for test for navigation to this screen
                    val context = LocalContext.current
                    finishAffinity(context as Activity)
                }
            }
        }
    }
    @Test
    fun `check bottom navigation bar and default display for a staff user`() {
        setHomeAsStartDestination() // empty list is created

        // Is the screen title displayed?
        rule.onNodeWithText(rule.activity.getString( R.string.staff_home)).assertIsDisplayed()
        // Is bottom navigation bar present?
        rule.onNode(hasContentDescription("bottom navigation bar")).assertIsDisplayed()
        val navItems = rule.onAllNodes(hasAnyAncestor(hasContentDescription("bottom navigation bar"))
                and hasClickAction())
        navItems.assertCountEquals(3) // confirm only 3 routes included

        val expectedRoutesForStaffUser = listOf(NavScreen.HOME.route, NavScreen.ADD.route,
            NavScreen.EXIT.route)
        expectedRoutesForStaffUser.forEach { route ->
            rule.onNodeWithText(route).assertExists()
        }
    }

    @Test
    fun `lazy list displays ticket assigned to the user`() {
        val testTicket = Ticket(
            title = "Test Ticket",
            createdByCustomerId = SIGNED_IN_USER_ID,
            updatedAt = Date()
        )

        setHomeAsStartDestination(listOf(testTicket))
        rule.onNodeWithText(testTicket.toString()).assertIsDisplayed()
    }

    // no value in tests for an empty list
    @Test
    fun `navigate to the add screen`() {
        setHomeAsStartDestination() // empty list is created
        rule.onNodeWithText(NavScreen.ADD.route).performClick()

        // We should now be on the add screen
        rule.onNodeWithText(rule.activity.getString(R.string.add_ticket)).assertIsDisplayed()
        // You could check for other elements specific to the add screen here as well but they are also checked by the add screen tests
    }
    @Test
    fun `navigate to the exit screen`() {
        setHomeAsStartDestination() // empty list is created
        rule.onNodeWithText(NavScreen.EXIT.route).performClick()
        rule.runOnIdle {
            assertTrue(rule.activity.isFinishing || rule.activity.isDestroyed)
        }
    }
}