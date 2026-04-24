package com.example.authenticationexample.data.ticket

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Ticket(
    @DocumentId
    val uid: String = "",
    val title: String = "",
    val description: String = "",
    val status: Status = Status.OPEN,
    val createdByCustomerId: String = "",
    val assignedToSupportId: String? = "",
    @ServerTimestamp
    val createdAt: Date? = null,
    @ServerTimestamp
    val updatedAt: Date? = null
){
    override fun toString(): String {
        val updated =
            SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault()).format(Date())

        return "$title \nLast updated:$updated [$status]"
    }
}