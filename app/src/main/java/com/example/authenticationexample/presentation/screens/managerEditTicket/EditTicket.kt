package com.example.authenticationexample.presentation.screens.managerEditTicket

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.authenticationexample.R
import com.example.authenticationexample.data.ticket.Status
import com.example.authenticationexample.data.ticket.Ticket
import com.example.authenticationexample.data.user.UserRole
import com.example.authenticationexample.presentation.components.BottomNavBar
import com.example.authenticationexample.presentation.components.CustomButton
import com.example.authenticationexample.presentation.components.CustomDropDownMenu
import com.example.authenticationexample.presentation.components.CustomTextField
import com.example.authenticationexample.presentation.components.SmallSpacer
import com.example.authenticationexample.presentation.screens.home.ItemView

@Composable
fun ManagerEditTicket(modifier: Modifier = Modifier,
                      titleText: String,
                      vm: EditTicketViewModel = hiltViewModel(),
                      selectedTicket: Ticket,
                      returnToHomeScreen: () -> Unit,
                      userRole: UserRole,
                      navController: NavHostController,
) {
    val keyboard = LocalSoftwareKeyboardController.current



    LaunchedEffect(selectedTicket) {
        vm.getTicket(selectedTicket)
    }


    Scaffold(
        modifier = modifier,
        bottomBar = {
            BottomNavBar(userRole, navController = navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(innerPadding),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = titleText,
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
            )

            SmallSpacer()
            Text(
                text = vm.uiState.ticket.title,
                fontWeight = FontWeight.Bold
            )

            SmallSpacer()
            Text(
                text = vm.uiState.ticket.description,
                textAlign = TextAlign.Center
            )


            SmallSpacer()
            CustomDropDownMenu(
                dropDownTitle = "Ticket Status",
                options = Status.entries,
                selectedValue = vm.uiState.ticket.status.displayName(),
                functionToDisplayItems = { it.displayName() },
                onChange = { vm.onChangeTicket(status = it) }
            )

            SmallSpacer()
            CustomButton(text = stringResource(R.string.update_status_of_ticket_button),
                clickButton = {
                    keyboard?.hide()
                    vm.updateTicket()
                    returnToHomeScreen()
                },
                enabled = vm.uiState.isTicketDescriptionValid()
            )

            SmallSpacer()
            LazyColumn {
                itemsIndexed(vm.notesForSelectedTicket) { index, item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        ItemView(
                            index = index,
                            item = item.toString(),
                            selected = item == vm.uiState.ticket,
                            onClick = {}
                        )
                    }
                }
            }

            SmallSpacer()
            CustomTextField(
                hintText = stringResource(R.string.note_text),
                text = vm.uiState.note,
                onValueChange = { vm.onChangeNote(note = it) },
                errorMessage = stringResource(R.string.note_text_error_message),
                errorPresent = !vm.uiState.isNoteValid(),
                maximumNumberOfLines = 10
            )

            CustomButton(text = stringResource(R.string.add_note_button),
                clickButton = {
                    keyboard?.hide()
                    vm.addNote()
                    returnToHomeScreen()
                },
                enabled = vm.uiState.isNoteValid()
            )
        }
    }
}