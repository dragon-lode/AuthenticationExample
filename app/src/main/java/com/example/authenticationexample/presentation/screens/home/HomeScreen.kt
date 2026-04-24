package com.example.authenticationexample.presentation.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.authenticationexample.data.user.UserRole
import com.example.authenticationexample.presentation.components.BottomNavBar
import com.example.authenticationexample.presentation.components.SmallSpacer

@Composable
fun HomeScreen(modifier: Modifier = Modifier,
               text: String,
               vm: HomeScreenViewModel = hiltViewModel(),
               userRole: UserRole,
               navController: NavHostController,
) {
    val items by vm.items.collectAsStateWithLifecycle()
    var selectedIndexToHighlight by remember { mutableIntStateOf(-1) }

    Scaffold(
            modifier = modifier,
            bottomBar = {
                BottomNavBar(userRole, navController = navController)
            }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize()
                                .padding(innerPadding),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = text,
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
            )

            SmallSpacer()
            LazyColumn {
                itemsIndexed(items) { index, item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        ItemView(
                            index = index,
                            item = item.toString(),
                            selected = selectedIndexToHighlight == index,
                            onClick = { index ->
                                vm.selectedTicket = item
                                //do something with the selected item
                            }
                        )
                    }
                }
            }

        }
    }
}