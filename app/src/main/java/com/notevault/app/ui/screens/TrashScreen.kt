package com.notevault.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notevault.app.ui.theme.VaultBg
import com.notevault.app.ui.theme.VaultCard
import com.notevault.app.ui.theme.VaultMint
import com.notevault.app.ui.theme.VaultSurface2
import com.notevault.app.ui.theme.VaultText
import com.notevault.app.ui.theme.VaultTextFaint
import com.notevault.app.ui.theme.VaultTextMuted
import com.notevault.app.viewmodel.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrashScreen(viewModel: NoteViewModel, onBack: () -> Unit) {
    val trash by viewModel.trash.collectAsStateWithLifecycle()
    var showEmptyDialog by remember { mutableStateOf(false) }

    if (showEmptyDialog) {
        AlertDialog(
            onDismissRequest = { showEmptyDialog = false },
            containerColor   = VaultSurface2,
            title = { Text("Empty Trash?", color = VaultText) },
            text  = { Text("All notes in trash will be permanently deleted.", color = VaultTextMuted) },
            confirmButton = {
                TextButton(onClick = { viewModel.emptyTrash(); showEmptyDialog = false }) {
                    Text("Empty", color = Color(0xFFFF6B6B))
                }
            },
            dismissButton = {
                TextButton(onClick = { showEmptyDialog = false }) {
                    Text("Cancel", color = VaultTextMuted)
                }
            }
        )
    }

    Scaffold(
        containerColor = VaultBg,
        topBar = {
            TopAppBar(
                title = { Text("Trash", color = VaultText) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = null, tint = VaultTextMuted)
                    }
                },
                actions = {
                    if (trash.isNotEmpty()) {
                        TextButton(onClick = { showEmptyDialog = true }) {
                            Text("Empty all", color = Color(0xFFFF6B6B))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VaultBg)
            )
        }
    ) { padding ->
        if (trash.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.DeleteOutline,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = VaultTextFaint
                    )
                    Text("Trash is empty", color = VaultTextMuted)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(trash, key = { it.id }) { note ->
                    Card(
                        shape  = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = VaultCard),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            if (note.title.isNotBlank()) {
                                Text(
                                    text  = note.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = VaultText
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                            if (note.content.isNotBlank()) {
                                Text(
                                    text     = note.content,
                                    maxLines = 2,
                                    color    = VaultTextMuted,
                                    style    = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = { viewModel.restore(note.id) },
                                    shape  = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = VaultMint)
                                ) {
                                    Text("Restore")
                                }
                                TextButton(onClick = { viewModel.permanentlyDelete(note.id) }) {
                                    Text("Delete forever", color = Color(0xFFFF6B6B))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
