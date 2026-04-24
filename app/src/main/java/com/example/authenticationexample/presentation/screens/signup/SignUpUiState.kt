package com.example.authenticationexample.presentation.screens.signup

import android.util.Patterns

data class SignUpUiState(
        var firstName: String = "",
        var surname: String = "",
        var email: String = "",
        var password: String = ""
)
{
    fun firstNameIsInvalid(): Boolean{
        return firstName.length < 3
    }
    fun surnameIsInvalid(): Boolean {
        return surname.length < 3
    }
    fun emailIsInvalid(): Boolean {
        return !Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun passwordIsInvalid(): Boolean {
        return password.length < 6
    }

    fun isValid(): Boolean {
        return  !firstNameIsInvalid() && !surnameIsInvalid() && !emailIsInvalid() && !passwordIsInvalid()

    }
}