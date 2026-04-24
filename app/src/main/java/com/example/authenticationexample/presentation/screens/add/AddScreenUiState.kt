package com.example.authenticationexample.presentation.screens.add;

data class AddScreenUiState(
        var description: String = "",
        var title: String = ""
)
{
    fun descriptionIsValid(): Boolean {
        return description.length < 10
    }

    fun titleIsInvalid(): Boolean {
        return title.length < 5
    }

    fun isValid(): Boolean {
        return !descriptionIsValid() && !titleIsInvalid()
    }
}
