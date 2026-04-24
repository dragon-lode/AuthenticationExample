package com.example.authenticationexample.navigation

import androidx.lifecycle.ViewModel
import com.example.authenticationexample.data.AuthRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val auth: AuthRepo
) : ViewModel() {
    fun signOut() {
        auth.signOut()
    }
}