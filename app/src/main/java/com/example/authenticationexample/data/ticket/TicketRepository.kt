package com.example.authenticationexample.data.ticket

import com.example.authenticationexample.data.Repository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface TicketRepo : Repository<Ticket> {
    suspend fun addNoteToTicket(ticketId: String, note: NoteForTicket)
    suspend fun getAllNotesForTicket(ticketId: String): List<NoteForTicket>
    fun findByUserId(id: String): Flow<List<Ticket>>

}
class TicketRepository @Inject constructor(
    private val dao: TicketDao
) : TicketRepo {

    override suspend fun addNoteToTicket(ticketId: String, note: NoteForTicket)
        = dao.addNoteToTicket(ticketId, note)

    override suspend fun getAllNotesForTicket(ticketId: String): List<NoteForTicket>
        = dao.getAllNotesForTicket(ticketId)

    override suspend fun delete(id: String) = dao.delete(id)
    override suspend fun insert(item: Ticket) = dao.add(item)
    override suspend fun update(item: Ticket) = dao.update(item)

    override fun findAll(): Flow<List<Ticket>> = dao.getAll()
    override suspend fun findById(id: String): Ticket? = dao.getById(id)
    override fun findByUserId(id: String): Flow<List<Ticket>> = dao.getAllByCustomerId(id)
}
