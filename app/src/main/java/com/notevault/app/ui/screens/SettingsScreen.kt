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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Clear
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import com.notevault.app.data.SettingsDataStore
import com.notevault.app.viewmodel.SettingsViewModel
import com.notevault.app.viewmodel.SettingsViewModelFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.notevault.app.ui.theme.VaultAccent
import com.notevault.app.ui.theme.VaultBg
import com.notevault.app.ui.theme.VaultCard
import com.notevault.app.ui.theme.VaultText
import com.notevault.app.ui.theme.VaultTextFaint
import com.notevault.app.ui.theme.VaultTextMuted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit, settingsDataStore: SettingsDataStore? = null) {
    val viewModel: SettingsViewModel = if (settingsDataStore != null) {
        viewModel(factory = SettingsViewModelFactory(settingsDataStore))
    } else {
        viewModel(factory = SettingsViewModelFactory(SettingsDataStore(androidx.compose.ui.platform.LocalContext.current)))
    }
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
                SettingSection(title = "Privacy") {
                    SettingCard(icon = Icons.Rounded.Lock, title = "No Trackers", subtitle = "Zero analytics, zero logs")
                    SettingCard(icon = Icons.Rounded.PhoneAndroid, title = "Local Storage Only", subtitle = "All data stays on this device")
                    SettingCard(icon = Icons.Rounded.CloudOff, title = "No Cloud Sync", subtitle = "Notes never leave your device")
                }
            }
            item {
                SettingSection(title = "About") {
                    SettingCard(icon = Icons.Rounded.Info, title = "NoteVault", subtitle = "Version 1.0.1 — Private by design")
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
fun SettingCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    showClearButton: Boolean = false,
    onClear: () -> Unit = {}
) {
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
        if (showClearButton) {
            IconButton(onClick = onClear) {
                Icon(
                    imageVector = Icons.Rounded.Clear,
                    contentDescription = "Clear",
                    tint = VaultTextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
