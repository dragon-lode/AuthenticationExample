package com.example.authenticationexample.navigation

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.authenticationexample.R
import com.example.authenticationexample.data.ticket.Ticket
import com.example.authenticationexample.data.user.UserRole
import com.example.authenticationexample.presentation.screens.add.AddScreen
import com.example.authenticationexample.presentation.screens.home.HomeScreen
import com.example.authenticationexample.presentation.screens.login.LoginScreen
import com.example.authenticationexample.presentation.screens.managerEditTicket.ManagerEditTicket
import com.example.authenticationexample.presentation.screens.managerHome.ManagerHomeScreen
import com.example.authenticationexample.presentation.screens.signup.SignUpScreen
import kotlin.system.exitProcess

@Composable
fun NavigationGraph(modifier: Modifier = Modifier,
                     navController: NavHostController = rememberNavController()
) {
    var userRole by remember { mutableStateOf(UserRole.UNKNOWN) } //Use on other screens to determine authorisation level or nav bar options

    var selectedTicket: Ticket? = null

    NavHost(navController,
        startDestination = NavScreen.LOGIN.route) {

        composable(NavScreen.LOGIN.route) {
            LoginScreen(
                navigateToSignUpScreen = {
                    navController.navigate(NavScreen.SIGNUP.route)
                },
                navigateToHomeScreen = {
                    //Destination screen depends on assigned user role
                    if (userRole == UserRole.MANAGER) {
                        navController.navigate(NavScreen.MANAGER_HOME.route)
                    } else if (userRole == UserRole.STAFF) {
                        navController.navigate(NavScreen.HOME.route)
                    }
                },
                //Allows the home screen to update the userRole here
                updateRoleForUser = { newUserRole ->
                    userRole = newUserRole
                },
                modifier = modifier
            )
        }

        composable(NavScreen.SIGNUP.route) {
            SignUpScreen(
                navigateBack = {
                    navController.popBackStack()
                },
                modifier = modifier
            )
        }

        composable(NavScreen.HOME.route) {
            HomeScreen(modifier = modifier,
                        text = stringResource(R.string.staff_home),
                        userRole = userRole, //Used to determine nav bar options in BottomNavBar
                        navController = navController,
                        )
        }

        composable(NavScreen.ADD.route) {
            AddScreen(modifier = modifier,
                text = stringResource(R.string.add_ticket),
                userRole = userRole, //Used to determine nav bar options in BottomNavBar
                navController = navController,
            )
        }

        composable(NavScreen.MANAGER_HOME.route) {
            ManagerHomeScreen( modifier = modifier,
                                text = stringResource(R.string.manager_home),
                                userRole = userRole, //Used to determine nav bar options in BottomNavBar
                                navController = navController,
                                selectTicketFromList = {
                                    selectedTicket = it
                                },
                                onClickGoToEditTicketScreen = {
                                    navController.navigate(NavScreen.MANAGER_EDIT_TICKET.route)
                                }
            )
        }

        composable(NavScreen.MANAGER_EDIT_TICKET.route) {
            ManagerEditTicket( modifier = modifier,
                                titleText = stringResource(R.string.manager_edit_ticket),
                                selectedTicket = selectedTicket!!,
                                returnToHomeScreen = {
                                    navController.popBackStack()
                                },
                                userRole = userRole, //Used to determine nav bar options in BottomNavBar
                                navController = navController
            )
        }

        composable(NavScreen.EXIT.route) {
            val authViewModel: AuthViewModel = hiltViewModel()
            authViewModel.signOut()
            //exitProcess(0)
            val context = LocalContext.current
            finishAffinity(context as Activity)
        }
    }
}