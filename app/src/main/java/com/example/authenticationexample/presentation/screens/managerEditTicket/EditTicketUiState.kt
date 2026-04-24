package com.example.authenticationexample.presentation.screens.managerEditTicket

import com.example.authenticationexample.data.ticket.Ticket

data class EditTicketUiState(
    var ticket: Ticket = Ticket(),
    var note: String = ""
) {

    fun isTicketDescriptionValid(): Boolean {
        return ticket.description.isNotBlank()
    }

    fun isNoteValid() : Boolean {
        return note.isNotBlank()
    }
}