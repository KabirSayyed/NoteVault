package com.notevault.app.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Archive
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.NoteAdd
import androidx.compose.material.icons.rounded.PushPin
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notevault.app.data.local.NoteEntity
import com.notevault.app.ui.theme.VaultAccent
import com.notevault.app.ui.theme.VaultBg
import com.notevault.app.ui.theme.VaultCard
import com.notevault.app.ui.theme.VaultDivider
import com.notevault.app.ui.theme.VaultSurface
import com.notevault.app.ui.theme.VaultSurface2
import com.notevault.app.ui.theme.VaultText
import com.notevault.app.ui.theme.VaultTextFaint
import com.notevault.app.ui.theme.VaultTextMuted
import com.notevault.app.viewmodel.NoteViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: NoteViewModel,
    onNew: () -> Unit,
    onOpen: (Long) -> Unit,
    onTrash: () -> Unit,
    onArchive: () -> Unit,
    onSettings: () -> Unit,
    onAi: () -> Unit
) {
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = VaultBg,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNew,
                containerColor = VaultAccent,
                contentColor = VaultText,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.size(60.dp)
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "New note", modifier = Modifier.size(28.dp))
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(VaultAccent),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "N",
                                color = VaultText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        Text(
                            text = "NoteVault",
                            style = MaterialTheme.typography.headlineSmall,
                            color = VaultText
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onAi) {
                        Icon(Icons.Rounded.AutoAwesome, contentDescription = "AI", tint = VaultAccent)
                    }
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Rounded.MoreVert, contentDescription = "Menu", tint = VaultTextMuted)
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text("Archive", color = VaultText) },
                            onClick = { showMenu = false; onArchive() },
                            leadingIcon = {
                                Icon(Icons.Rounded.Archive, contentDescription = null, tint = VaultTextMuted)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Trash", color = VaultText) },
                            onClick = { showMenu = false; onTrash() },
                            leadingIcon = {
                                Icon(Icons.Rounded.DeleteOutline, contentDescription = null, tint = VaultTextMuted)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Settings", color = VaultText) },
                            onClick = { showMenu = false; onSettings() },
                            leadingIcon = {
                                Icon(Icons.Rounded.Settings, contentDescription = null, tint = VaultTextMuted)
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VaultBg)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.setSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(20.dp),
                placeholder = { Text("Search notes...", color = VaultTextFaint) },
                leadingIcon = {
                    Icon(Icons.Rounded.Search, contentDescription = null, tint = VaultTextMuted)
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Rounded.Close, contentDescription = null, tint = VaultTextMuted)
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = VaultAccent,
                    unfocusedBorderColor = VaultDivider,
                    focusedContainerColor   = VaultSurface,
                    unfocusedContainerColor = VaultSurface,
                    focusedTextColor   = VaultText,
                    unfocusedTextColor = VaultText,
                    cursorColor        = VaultAccent
                ),
                singleLine = true
            )

            if (notes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.NoteAdd,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = VaultTextFaint
                        )
                        Text(
                            text = if (query.isEmpty()) "Your vault is empty" else "No notes found",
                            color = VaultTextMuted,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        if (query.isEmpty()) {
                            Text(
                                text = "Tap + to write your first note",
                                color = VaultTextFaint,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            } else {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Adaptive(160.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalItemSpacing = 10.dp
                ) {
                    items(notes, key = { it.id }) { note ->
                        NoteCard(
                            note = note,
                            onClick = { onOpen(note.id) },
                            onLongClick = { viewModel.togglePin(note.id, note.isPinned) }
                        )
                    }
                    item(span = StaggeredGridItemSpan.FullLine) {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteCard(
    note: NoteEntity,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val cardColor = Color(note.colorHex)
    val fmt = remember { SimpleDateFormat("MMM dd", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (note.isPinned) {
                Icon(
                    imageVector = Icons.Rounded.PushPin,
                    contentDescription = "Pinned",
                    tint = VaultAccent,
                    modifier = Modifier.size(14.dp)
                )
            }
            if (note.title.isNotBlank()) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = VaultText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.SemiBold
                )
            }
            if (note.content.isNotBlank()) {
                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = VaultTextMuted,
                    maxLines = 8,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = fmt.format(Date(note.updatedAt)),
                style = MaterialTheme.typography.labelMedium,
                color = VaultTextFaint
            )
        }
    }
}
