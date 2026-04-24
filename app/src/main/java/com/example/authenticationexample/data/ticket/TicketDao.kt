package com.example.authenticationexample.data.ticket

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TicketDao @Inject constructor(private val firestore: FirebaseFirestore
) {
    private val ticketCollection = firestore.collection("tickets")

    suspend fun add(ticket: Ticket) {
        val newDocRef = ticketCollection.document()
        val ticketWithId = ticket.copy(uid = newDocRef.id)
        newDocRef.set(ticketWithId).await()
       // ticketCollection.add(ticket).await().set(ticket).await()
    }

    suspend fun update(ticket: Ticket) {
        ticketCollection.document(ticket.uid).set(ticket).await()
    }

    suspend fun delete(ticketId: String) {
        val ticketRef = ticketCollection.document(ticketId)
        for (note in ticketRef.collection("notesForTicket").get().await()) {
            note.reference.delete().await()
        }
        //delete ticket
        ticketRef.delete().await()
    }

    fun getAll(): Flow<List<Ticket>> {
        return ticketCollection.snapshots()
            .map { snapshot ->
                snapshot.toObjects(Ticket::class.java)
            }.catch { e ->
                emit(emptyList())
            }
    }

    suspend fun getById(id: String): Ticket? {
        val snapshot = ticketCollection.document(id).get().await()
        return if (snapshot.exists()) {
            snapshot.toObject(Ticket::class.java)
        } else {
            null
        }
    }

    fun getAllByCustomerId(id: String) : Flow<List<Ticket>> {
        return ticketCollection.whereEqualTo("createdByCustomerId", id)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(Ticket::class.java)
            }.catch { e ->
                emit(emptyList())
            }
    }

    suspend fun addNoteToTicket(ticketId: String, note: NoteForTicket) {
        ticketCollection.document(ticketId)
            .collection("notesForTicket")
            .add(note)
            .await()
    }

    suspend fun getAllNotesForTicket(ticketId: String): List<NoteForTicket> {
        return ticketCollection.document(ticketId)
            .collection("notesForTicket")
            .get()
            .await()
            .toObjects(NoteForTicket::class.java)
    }
}