package com.notevault.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.notevault.app.data.FpsMode
import com.notevault.app.data.SettingsDataStore
import com.notevault.app.ui.theme.VaultAccent
import com.notevault.app.ui.theme.VaultBg
import com.notevault.app.ui.theme.VaultCard
import com.notevault.app.ui.theme.VaultText
import com.notevault.app.ui.theme.VaultTextFaint
import com.notevault.app.ui.theme.VaultTextMuted
import com.notevault.app.viewmodel.SettingsViewModel
import com.notevault.app.viewmodel.SettingsViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit, settingsDataStore: SettingsDataStore? = null) {
    val viewModel: SettingsViewModel = if (settingsDataStore != null) {
        viewModel(factory = SettingsViewModelFactory(settingsDataStore))
    } else {
        viewModel(factory = SettingsViewModelFactory(SettingsDataStore(androidx.compose.ui.platform.LocalContext.current)))
    }

    val fpsMode = viewModel.fpsMode.collectAsState()

    Scaffold(
        containerColor = VaultBg,
        topBar = {
            TopAppBar(
                title = { Text("Settings", color = VaultText) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = null, tint = VaultTextMuted)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VaultBg)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                SettingSection(title = "Performance") {
                    FpsSettings(currentFpsMode = fpsMode.value, onFpsChange = { viewModel.setFpsMode(it) })
                }
            }
            item {
                SettingSection(title = "Privacy") {
                    SettingCard(Icons.Rounded.Lock,        "No Trackers",       "Zero analytics, zero logs")
                    SettingCard(Icons.Rounded.PhoneAndroid,"Local Storage Only","All data stays on this device")
                    SettingCard(Icons.Rounded.CloudOff,    "No Cloud Sync",     "Notes never leave your device")
                }
            }
            item {
                SettingSection(title = "AI Assistant") {
                    SettingCard(Icons.Rounded.AutoAwesome, "Optional AI",  "Add your own API key to enable")
                    SettingCard(Icons.Rounded.Key,         "API Key",      "Stored locally, only sent to your provider")
                }
            }
            item {
                SettingSection(title = "About") {
                    SettingCard(Icons.Rounded.Info, "NoteVault", "Version 1.0 — Private by design")
                }
            }
        }
    }
}

@Composable
fun SettingSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape  = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = VaultCard)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(title, style = MaterialTheme.typography.labelMedium, color = VaultAccent)
            content()
        }
    }
}

@Composable
fun SettingCard(icon: ImageVector, title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = VaultTextMuted,
            modifier = Modifier.size(24.dp)
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = VaultText)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = VaultTextFaint)
        }
    }
}

@Composable
fun ColumnScope.FpsSettings(currentFpsMode: FpsMode, onFpsChange: (FpsMode) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Display Refresh Rate",
            style = MaterialTheme.typography.bodySmall,
            color = VaultTextMuted,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        FpsOption(
            mode = FpsMode.LOW_FPS,
            isSelected = currentFpsMode == FpsMode.LOW_FPS,
            onSelect = { onFpsChange(FpsMode.LOW_FPS) }
        )

        FpsOption(
            mode = FpsMode.FPS_60,
            isSelected = currentFpsMode == FpsMode.FPS_60,
            onSelect = { onFpsChange(FpsMode.FPS_60) }
        )

        FpsOption(
            mode = FpsMode.FPS_90,
            isSelected = currentFpsMode == FpsMode.FPS_90,
            onSelect = { onFpsChange(FpsMode.FPS_90) }
        )
    }
}

@Composable
fun FpsOption(mode: FpsMode, isSelected: Boolean, onSelect: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .selectable(
                selected = isSelected,
                onClick = onSelect,
                role = Role.RadioButton
            )
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = null,
            colors = RadioButtonDefaults.colors(
                selectedColor = VaultAccent,
                unselectedColor = VaultTextMuted
            )
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(mode.displayName, style = MaterialTheme.typography.titleMedium, color = VaultText)
        }
    }
}
