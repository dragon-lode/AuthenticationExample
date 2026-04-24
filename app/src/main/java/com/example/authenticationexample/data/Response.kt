package com.example.authenticationexample.data

sealed class Response{
    data object Startup: Response()

    data object Loading: Response()

    data object NotConfirmed: Response()
    data object Success: Response()

    data class Failure(
        val e: Exception
    ): Response()
}