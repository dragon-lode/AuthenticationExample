package com.example.authenticationexample.presentation.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.example.authenticationexample.data.user.UserRole
import com.example.authenticationexample.presentation.components.CustomButton
import com.example.authenticationexample.presentation.components.ProgressBar
import com.example.authenticationexample.presentation.components.SmallSpacer
import com.example.authenticationexample.presentation.components.CustomTextField

@Composable
fun LoginScreen(modifier: Modifier = Modifier,
                vm: LoginViewModel = hiltViewModel(),
                updateRoleForUser: (UserRole) -> Unit,
                navigateToSignUpScreen: () -> Unit,
                navigateToHomeScreen: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        vm.uiEvents.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    val response = vm.signInResponse

    if (response is Response.Success) {
        LaunchedEffect(response) {
            if (vm.isEmailVerified) {
                updateRoleForUser(vm.userRole)
                navigateToHomeScreen()
            } else {
                snackbarHostState.showSnackbar("Email not verified")
            }
        }
   }
    if (response is Response.Failure){
        LaunchedEffect(response) {
            snackbarHostState.showSnackbar(response.e.message ?: "An unexpected error occurred")
        }
    }

    Scaffold(snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) },
        content = { padding ->
        val keyboard = LocalSoftwareKeyboardController.current

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
                CustomTextField(
                    hintText = stringResource(R.string.email),
                    text = vm.loginUiState.email,
                    onValueChange = { vm.onChange(email = it) },
                    errorMessage = stringResource(R.string.email_error_message),
                    errorPresent = vm.loginUiState.emailIsInvalid()
                )
                SmallSpacer()
                CustomTextField(
                    hintText = stringResource(R.string.password),
                    text = vm.loginUiState.password,
                    isPasswordField = true,
                    onValueChange = { vm.onChange(password = it) },
                    errorMessage = stringResource(R.string.password_error_message),
                    errorPresent = vm.loginUiState.passwordIsInvalid()
                )

                SmallSpacer()
                CustomButton(
                    stringResource(R.string.submit_button),
                    clickButton = {
                        keyboard?.hide()
                        vm.signInWithEmailAndPassword()
                    },
                    enabled = vm.loginUiState.isValid()
                )

                SmallSpacer()
                CustomButton(
                    stringResource(R.string.forgot_password),
                    clickButton = { vm.forgotPassword() },
                    enabled = !vm.loginUiState.emailIsInvalid()
                )

                SmallSpacer()
                CustomButton(
                    stringResource(R.string.sign_up_button),
                    clickButton = { navigateToSignUpScreen() }
                )
            }
        }
    )
    //Layout on top of scaffold
    if (response is Response.Loading) {
        ProgressBar()
    }
}