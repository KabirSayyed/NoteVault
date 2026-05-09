package com.notevault.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.notevault.app.data.local.NoteEntity
import com.notevault.app.data.repository.NoteRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NoteViewModel(private val repository: NoteRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val notes: StateFlow<List<NoteEntity>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) repository.getNotes()
            else repository.search(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val trash: StateFlow<List<NoteEntity>> = repository.getTrash()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val archived: StateFlow<List<NoteEntity>> = repository.getArchived()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun saveNote(
        id: Long = 0L,
        title: String,
        content: String,
        colorHex: Long = 0xFF1E1E30,
        isPinned: Boolean = false
    ) {
        viewModelScope.launch {
            repository.save(
                NoteEntity(
                    id = id,
                    title = title,
                    content = content,
                    colorHex = colorHex,
                    isPinned = isPinned
                )
            )
        }
    }

    fun moveToTrash(id: Long) = viewModelScope.launch { repository.moveToTrash(id) }

    fun restore(id: Long) = viewModelScope.launch { repository.restore(id) }

    fun permanentlyDelete(id: Long) = viewModelScope.launch { repository.permanentlyDelete(id) }

    fun emptyTrash() = viewModelScope.launch { repository.emptyTrash() }

    fun togglePin(id: Long, currentPinState: Boolean) = viewModelScope.launch {
        repository.setPin(id, !currentPinState)
    }

    fun toggleArchive(id: Long, currentArchiveState: Boolean) = viewModelScope.launch {
        repository.setArchive(id, !currentArchiveState)
    }

    suspend fun getById(id: Long): NoteEntity? = repository.getById(id)
}

class NoteViewModelFactory(private val repository: NoteRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NoteViewModel(repository) as T
    }
}
