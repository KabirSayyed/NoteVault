package com.notevault.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val SETTINGS_DATASTORE = "settings_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = SETTINGS_DATASTORE)

object SettingsKeys {
    val FPS_MODE = stringPreferencesKey("fps_mode")
}

enum class FpsMode(val displayName: String) {
    LOW_FPS("Low FPS - Battery Saver"),
    FPS_60("60 FPS - Balanced"),
    FPS_90("90 FPS - Smooth")
}

class SettingsDataStore(private val context: Context) {

    val fpsModeFlow: Flow<FpsMode> = context.dataStore.data.map { preferences ->
        val fpsModeString = preferences[SettingsKeys.FPS_MODE] ?: FpsMode.FPS_60.name
        FpsMode.valueOf(fpsModeString)
    }

    suspend fun setFpsMode(fpsMode: FpsMode) {
        context.dataStore.edit { preferences ->
            preferences[SettingsKeys.FPS_MODE] = fpsMode.name
        }
    }

}
