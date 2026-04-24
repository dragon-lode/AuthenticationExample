package com.example.authenticationexample.presentation.screens.managerEditTicket

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.authenticationexample.data.AuthRepo
import com.example.authenticationexample.data.ticket.NoteForTicket
import com.example.authenticationexample.data.ticket.Status
import com.example.authenticationexample.data.ticket.Ticket
import com.example.authenticationexample.data.ticket.TicketRepo
import com.example.authenticationexample.data.user.User
import com.example.authenticationexample.data.user.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject


@HiltViewModel
class EditTicketViewModel @Inject constructor(
    private val auth: AuthRepo,
    private val userRepo: UserRepo,
    private val ticketRepo: TicketRepo
) : ViewModel() {

    var uiState by mutableStateOf(EditTicketUiState())
     private set

    var notesForSelectedTicket by mutableStateOf<List<NoteForTicket>>(emptyList())
        private set

    var userName: String = ""

    init{
        viewModelScope.launch {
            val user = userRepo.findById(auth.getUserId() ?: "") ?: User()
            userName = "${user.firstName} ${user.surname}"
        }
    }

    fun getTicket(selectedTicket: Ticket){
        uiState = uiState.copy(ticket = selectedTicket) //trigger a UI change
        viewModelScope.launch(errorHandler) {
            notesForSelectedTicket = ticketRepo.getAllNotesForTicket(selectedTicket.uid)
        }
    }
    fun onChangeTicket(status: Status) {
        val updatedTicket = uiState.ticket.copy(status = status, updatedAt = Date())
        uiState = uiState.copy(ticket = updatedTicket) //trigger a UI change
    }

    fun onChangeNote(note: String){
        uiState = uiState.copy(note = note) //trigger a UI change
    }

    fun updateTicket(){
        viewModelScope.launch(errorHandler) {
            ticketRepo.update(uiState.ticket)
        }
    }

    fun addNote(){
        viewModelScope.launch(errorHandler) {
            val newNote = NoteForTicket(notes = uiState.note,
                                        createdById = auth.getUserId() ?: throw Exception("User ID not found"),
                                        creatorName = userName
            )
            ticketRepo.addNoteToTicket(ticketId = uiState.ticket.uid, note = newNote)
        }
    }

    val errorHandler = CoroutineExceptionHandler { _, exception ->
        Log.e("ManagerEditTicketViewModel", "Update error: ${exception.message}")
    }
}