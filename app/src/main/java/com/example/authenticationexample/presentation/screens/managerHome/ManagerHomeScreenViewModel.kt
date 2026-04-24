package com.example.authenticationexample.presentation.screens.managerHome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.authenticationexample.data.AuthRepo
import com.example.authenticationexample.data.ticket.Ticket
import com.example.authenticationexample.data.ticket.TicketRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManagerHomeScreenViewModel @Inject constructor(
    private val ticketRepo: TicketRepo
) : ViewModel(){
    val items: StateFlow<List<Ticket>> = ticketRepo.findAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    var selectedTicket: Ticket?= null

    fun deleteTicket(){
        viewModelScope.launch {
            ticketRepo.delete(selectedTicket?.uid ?: "")
        }
    }
}