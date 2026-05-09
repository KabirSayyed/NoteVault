package com.notevault.app

import android.app.Application
import androidx.room.Room
import com.notevault.app.data.local.NoteDatabase
import com.notevault.app.data.repository.NoteRepository

class NoteVaultApp : Application() {

    lateinit var database: NoteDatabase
        private set

    lateinit var repository: NoteRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            NoteDatabase::class.java,
            "notevault_db"
        ).build()
        repository = NoteRepository(database.noteDao())
    }
}
