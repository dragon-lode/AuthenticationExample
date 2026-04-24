package com.example.authenticationexample.presentation.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.authenticationexample.data.user.UserRole
import com.example.authenticationexample.navigation.NavScreen

private fun createListOfItems(userRole: UserRole): List<NavScreen> {
    val navScreenItemsForUserRole = when (userRole) {
        UserRole.MANAGER -> listOf(NavScreen.MANAGER_HOME)
        UserRole.STAFF -> listOf(NavScreen.HOME, NavScreen.ADD)
        UserRole.UNKNOWN -> emptyList()
    }
    return navScreenItemsForUserRole + NavScreen.EXIT
}

@Composable
fun BottomNavBar(userRole: UserRole,
                 navController: NavController) {
    NavigationBar(
        containerColor = Color.White,
        contentColor = Color.Black
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        createListOfItems(userRole).forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any {
                it.route == item.route
            } == true

            NavigationBarItem(
                selected = isSelected,
                label = {
                    Text(text = item.route, fontSize = 9.sp)
                },
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.route
                    )
                },
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
