package com.notevault.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.notevault.app.data.FpsMode
import com.notevault.app.data.SettingsDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val settingsDataStore: SettingsDataStore) : ViewModel() {

    val fpsMode: StateFlow<FpsMode> = settingsDataStore.fpsModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), FpsMode.FPS_60)

    fun setFpsMode(fpsMode: FpsMode) {
        viewModelScope.launch {
            settingsDataStore.setFpsMode(fpsMode)
        }
    }

}

class SettingsViewModelFactory(private val settingsDataStore: SettingsDataStore) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingsViewModel(settingsDataStore) as T
    }
}
