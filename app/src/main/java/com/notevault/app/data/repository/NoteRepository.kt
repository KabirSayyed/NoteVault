package com.notevault.app.data.repository

import com.notevault.app.data.local.NoteDao
import com.notevault.app.data.local.NoteEntity
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val dao: NoteDao) {

    fun getNotes(): Flow<List<NoteEntity>> = dao.getActiveNotes()

    fun getTrash(): Flow<List<NoteEntity>> = dao.getDeletedNotes()

    fun getArchived(): Flow<List<NoteEntity>> = dao.getArchivedNotes()

    fun search(query: String): Flow<List<NoteEntity>> = dao.searchNotes(query)

    suspend fun getById(id: Long): NoteEntity? = dao.getNoteById(id)

    suspend fun save(note: NoteEntity): Long {
        return if (note.id == 0L) {
            dao.insert(note)
        } else {
            dao.update(note.copy(updatedAt = System.currentTimeMillis()))
            note.id
        }
    }

    suspend fun moveToTrash(id: Long) = dao.moveToTrash(id)

    suspend fun restore(id: Long) = dao.restore(id)

    suspend fun setPin(id: Long, pinned: Boolean) = dao.setPin(id, pinned)

    suspend fun setArchive(id: Long, archived: Boolean) = dao.setArchive(id, archived)

    suspend fun permanentlyDelete(id: Long) = dao.permanentlyDelete(id)

    suspend fun emptyTrash() = dao.emptyTrash()
}
