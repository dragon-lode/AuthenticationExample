package com.example.authenticationexample.presentation.screens.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.authenticationexample.data.user.UserRole
import com.example.authenticationexample.presentation.components.BottomNavBar
import com.example.authenticationexample.presentation.components.CustomButton
import com.example.authenticationexample.presentation.components.CustomTextField
import com.example.authenticationexample.presentation.components.SmallSpacer

@Composable
fun AddScreen(modifier: Modifier = Modifier,
               text: String,
               vm: AddScreenViewModel = hiltViewModel(),
               userRole: UserRole,
               navController: NavHostController,
) {
    val keyboard = LocalSoftwareKeyboardController.current

    Scaffold(
        modifier = modifier,
        bottomBar = {
            BottomNavBar(userRole, navController = navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = text,
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
            )

            CustomTextField(text = vm.uiState.title,
                onValueChange = { vm.onChange(title = it) },
                errorMessage = stringResource(R.string.title_error_message),
                errorPresent = vm.uiState.titleIsInvalid(),
                isPasswordField = false,
                hintText = stringResource(R.string.title_hint)
            )

            SmallSpacer()
            CustomTextField(text = vm.uiState.description,
                onValueChange = { vm.onChange(description = it) },
                errorMessage = stringResource(R.string.description_error_message),
                errorPresent = vm.uiState.descriptionIsValid(),
                isPasswordField = false,
                hintText = stringResource(R.string.description_hint),
                maximumNumberOfLines = 5
            )

            SmallSpacer()
            CustomButton(text = stringResource(R.string.submit_button),
                clickButton = {
                    keyboard?.hide()
                    vm.addTicket()
                },
                enabled = vm.uiState.isValid()
            )
        }
    }
}