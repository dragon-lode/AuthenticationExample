package com.example.authenticationexample.presentation.screens.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.authenticationexample.R
import com.example.authenticationexample.data.Response
import com.example.authenticationexample.presentation.components.CustomButton
import com.example.authenticationexample.presentation.components.ProgressBar
import com.example.authenticationexample.presentation.components.SmallSpacer
import com.example.authenticationexample.presentation.components.CustomTextField

@Composable
fun SignUpScreen(modifier: Modifier = Modifier,
                 vm: SignUpViewModel = hiltViewModel(),
                 navigateBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val keyboard = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        vm.uiEvents.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    val response = vm.signUpResponse

    Scaffold(snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) },
        content = { padding ->
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CustomTextField(
                    hintText = stringResource(R.string.first_name_hint),
                    text = vm.signUpUiState.firstName,
                    onValueChange = { vm.onChange(firstName = it) },
                    errorMessage = stringResource(R.string.first_name_error_message),
                    errorPresent = vm.signUpUiState.firstNameIsInvalid()
                )

                SmallSpacer()
                CustomTextField(
                    hintText = stringResource(R.string.surname_hint),
                    text = vm.signUpUiState.surname,
                    isPasswordField = false,
                    onValueChange = { vm.onChange(surname = it) },
                    errorMessage = stringResource(R.string.surname_error_message),
                    errorPresent = vm.signUpUiState.surnameIsInvalid()
                )

                SmallSpacer()
                CustomTextField(
                    hintText = stringResource(R.string.email),
                    text = vm.signUpUiState.email,
                    isPasswordField = false,
                    onValueChange = { vm.onChange(email = it) },
                    errorMessage = stringResource(R.string.email_error_message),
                    errorPresent = vm.signUpUiState.emailIsInvalid()
                )

                SmallSpacer()
                CustomTextField(
                    hintText = stringResource(R.string.password),
                    text = vm.signUpUiState.password,
                    isPasswordField = true,
                    onValueChange = { vm.onChange(password = it) },
                    errorMessage = stringResource(R.string.password_error_message),
                    errorPresent = vm.signUpUiState.passwordIsInvalid()
                )

                SmallSpacer()
                CustomButton(
                    stringResource(R.string.submit_button),
                    clickButton = {
                        keyboard?.hide()
                        vm.signUpWithEmailAndPassword()
                    },
                    enabled = vm.signUpUiState.isValid() && response !is Response.Loading //better version
                )
                Row {
                    CustomButton(stringResource(R.string.back_button),
                                    clickButton = { navigateBack() }
                    )
                }
            }
        }
    )
    //Layout on top of scaffold
    if (response is Response.Loading) {
        ProgressBar()
    }
}
