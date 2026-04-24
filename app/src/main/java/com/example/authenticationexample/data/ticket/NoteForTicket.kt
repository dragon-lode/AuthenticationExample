package com.example.authenticationexample.data.ticket

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class NoteForTicket(
    @DocumentId
    val uid: String = "",
    val notes: String = "",
    val createdById: String = "",
    val creatorName: String = "", //useful for displaying name of creator in lists
    @ServerTimestamp
    val createdAt: Date? = null
){
    override fun toString(): String {
        val created =
            SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault()).format(Date())
        return "$created by $creatorName \n $notes"
    }
}