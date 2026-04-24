package com.example.authenticationexample.presentation.screens.login

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.authenticationexample.data.AuthRepo
import com.example.authenticationexample.data.Response
import com.example.authenticationexample.data.user.UserRepo
import com.example.authenticationexample.data.user.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: AuthRepo,
    private val userRepo: UserRepo
) : ViewModel() {
    var loginUiState by mutableStateOf(LoginUiState())
    var userRole by mutableStateOf(UserRole.UNKNOWN)
        private set

    fun onChange(email: String = loginUiState.email,
                 password: String = loginUiState.password) {
        loginUiState = loginUiState.copy(email = email,
                                        password = password)
    }

    private val _uiEvents = Channel<String>()
    val uiEvents = _uiEvents.receiveAsFlow()

    val isEmailVerified get() = auth.currentUser?.isEmailVerified ?: false

    var signInResponse by mutableStateOf<Response>(Response.Startup)
        private set

    fun forgotPassword() {
        viewModelScope.launch {
            val message = when (auth.sendPasswordResetEmail(loginUiState.email)) {
                is Response.Success -> "Password reset email has been sent successfully"
                is Response.Failure ->  "Unable to send reset email"
                else -> "An unexpected error occurred"
            }
            _uiEvents.send(message)
        }
    }

    fun signInWithEmailAndPassword() {
        viewModelScope.launch {
            signInResponse = Response.Loading
            val response  = auth.signInWithEmailAndPassword(loginUiState.email, loginUiState.password)
            Log.v("OK",response.toString())
            if (response is Response.Success) {//auth has confirmed user login
                try {//get role from user table
                    val userId = auth.getUserId() ?: throw Exception("User ID not found after sign-in")
                    userRole = userRepo.getUserRole(userId)
                    signInResponse = response //Do not do this until after role is established (otherwise submit will need clicking twice)
                } catch (e: Exception){
                    Log.e("LoginViewModel", "Error fetching user role", e)
                    signInResponse = Response.Failure(e)
                }
            }

            if (signInResponse is Response.Failure) {
                _uiEvents.send("Unable to sign in: ${(signInResponse as Response.Failure).e.message}")
            }
        }
    }
}