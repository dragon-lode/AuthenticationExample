package com.example.authenticationexample.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.authenticationexample.data.AuthRepo
import com.example.authenticationexample.data.ticket.Ticket
import com.example.authenticationexample.data.ticket.TicketRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val auth: AuthRepo,
    private val ticketRepo: TicketRepo
) : ViewModel(){
    val userId = auth.getUserId() ?: throw Exception("User ID not found after sign-in")

    val items: StateFlow<List<Ticket>> = ticketRepo.findByUserId(userId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    var selectedTicket: Ticket?= null
}