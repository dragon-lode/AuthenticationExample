package com.example.authenticationexample.presentation.screens.login

import android.util.Patterns

data class LoginUiState(
        var email: String = "",
        var password: String = ""
)
{
    fun emailIsInvalid(): Boolean {
        return !Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun passwordIsInvalid(): Boolean {
        return password.length < 6
    }

    fun isValid(): Boolean {
        return  !emailIsInvalid() && !passwordIsInvalid()

    }
}