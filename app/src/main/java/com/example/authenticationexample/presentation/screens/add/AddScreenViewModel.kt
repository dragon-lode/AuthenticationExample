package com.example.authenticationexample.presentation.screens.add

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.authenticationexample.data.AuthRepo
import com.example.authenticationexample.data.ticket.Ticket
import com.example.authenticationexample.data.ticket.TicketRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddScreenViewModel @Inject constructor(
    private val auth: AuthRepo,
    private val ticketRepo: TicketRepo
) : ViewModel() {
    var uiState by mutableStateOf(AddScreenUiState())

    fun onChange(description: String = uiState.description,
                 title: String = uiState.title) {
        uiState = uiState.copy(description = description,
                                        title = title)
    }

    fun addTicket() {
        val userId = auth.getUserId() ?: throw Exception("User ID not found after sign-in")

        val newTicket = Ticket(
            title = uiState.title,
            description = uiState.description,
            createdByCustomerId = userId
        )
        viewModelScope.launch(errorHandler) {
            ticketRepo.insert(newTicket)
            clear()
        }
    }

    val errorHandler = CoroutineExceptionHandler { _, exception ->
        Log.e("AddScreenViewModel", "Insert error: ${exception.message}")
    }

    private fun clear(){
        uiState = AddScreenUiState() // Reset the contents
    }
}