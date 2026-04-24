package com.example.authenticationexample.presentation.screens.managerHome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.authenticationexample.R
import com.example.authenticationexample.data.ticket.Ticket
import com.example.authenticationexample.data.user.UserRole
import com.example.authenticationexample.presentation.components.BottomNavBar
import com.example.authenticationexample.presentation.components.CustomButton
import com.example.authenticationexample.presentation.components.SmallSpacer
import com.example.authenticationexample.presentation.screens.home.ItemView

@Composable
fun ManagerHomeScreen(modifier: Modifier = Modifier,
                      text: String,
                      vm: ManagerHomeScreenViewModel = hiltViewModel(),
                      userRole: UserRole,
                      navController: NavHostController,
                      selectTicketFromList: (Ticket?) -> Unit,
                      onClickGoToEditTicketScreen: () -> Unit
) {
    val keyboard = LocalSoftwareKeyboardController.current
    val items by vm.items.collectAsStateWithLifecycle()
    var selectedIndexToHighlight by remember { mutableIntStateOf(-1) }
    var showDeletionConfirmationDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            BottomNavBar(userRole, navController = navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                                selectedIndexToHighlight = index
                                selectTicketFromList(item)
                            }
                        )
                    }
                }
            }

            val isItemSelected =  selectedIndexToHighlight != -1 && items.isNotEmpty()
            SmallSpacer()
            CustomButton(text = stringResource(R.string.view_ticket_button),
                clickButton = {
                    keyboard?.hide()
                    onClickGoToEditTicketScreen()
                },
                enabled = isItemSelected
            )

            SmallSpacer()
            CustomButton(text = stringResource(R.string.delete_ticket_button),
                clickButton = {
                    showDeletionConfirmationDialog = true
                },
                enabled = isItemSelected
            )
        }

        if (showDeletionConfirmationDialog) {
            AlertDialog(
                onDismissRequest = { showDeletionConfirmationDialog = false },
                title = { Text(stringResource(R.string.confirm_deletion_dialog_heading)) },
                text = { Text(stringResource(R.string.confirm_deletion_dialog_message)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeletionConfirmationDialog = false
                            keyboard?.hide()
                            vm.deleteTicket()
                            selectedIndexToHighlight = -1
                        }
                    ) {
                        Text(stringResource(R.string.delete_ticket_button),
                                color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeletionConfirmationDialog = false }) {
                        Text(stringResource(R.string.cancel_deletion_button))
                    }
                }
            )
        }
    }
}