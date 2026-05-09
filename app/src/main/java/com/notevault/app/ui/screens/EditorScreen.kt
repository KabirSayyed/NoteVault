package com.notevault.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.notevault.app.ui.theme.NoteColor1
import com.notevault.app.ui.theme.NoteColorOptions
import com.notevault.app.ui.theme.VaultAccent
import com.notevault.app.ui.theme.VaultDivider
import com.notevault.app.ui.theme.VaultSurface2
import com.notevault.app.ui.theme.VaultText
import com.notevault.app.ui.theme.VaultTextFaint
import com.notevault.app.ui.theme.VaultTextMuted
import com.notevault.app.viewmodel.NoteViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    viewModel: NoteViewModel,
    noteId: Long,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(NoteColor1) }
    var isPinned by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var lastSaved by remember { mutableStateOf("") }
    val fmt = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    LaunchedEffect(noteId) {
        if (noteId != 0L) {
            val note = viewModel.getById(noteId)
            if (note != null) {
                title = note.title
                content = note.content
                selectedColor = Color(note.colorHex)
                isPinned = note.isPinned
            }
        }
    }

    fun doSave() {
        scope.launch {
            viewModel.saveNote(
                id       = noteId,
                title    = title,
                content  = content,
                colorHex = selectedColor.value.toLong(),
                isPinned = isPinned
            )
            lastSaved = "Saved at ${fmt.format(Date())}"
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor   = VaultSurface2,
            title  = { Text("Move to Trash?", color = VaultText) },
            text   = { Text("You can restore it from Trash later.", color = VaultTextMuted) },
            confirmButton = {
                TextButton(onClick = {
                    if (noteId != 0L) viewModel.moveToTrash(noteId)
                    showDeleteDialog = false
                    onBack()
                }) {
                    Text("Move to Trash", color = Color(0xFFFF6B6B))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = VaultTextMuted)
                }
            }
        )
    }

    Scaffold(
        containerColor = selectedColor,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = selectedColor),
                navigationIcon = {
                    // ✅ FIX 1: Back just goes back, no duplicate save
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = null, tint = VaultTextMuted)
                    }
                },
                title = {
                    if (lastSaved.isNotEmpty()) {
                        Text(
                            text  = lastSaved,
                            style = MaterialTheme.typography.labelMedium,
                            color = VaultTextFaint
                        )
                    }
                },
                actions = {
                    // ✅ FIX 2: Pin just toggles, no save triggered
                    IconButton(onClick = { isPinned = !isPinned }) {
                        Icon(
                            imageVector = Icons.Rounded.PushPin,
                            contentDescription = null,
                            tint = if (isPinned) VaultAccent else VaultTextFaint
                        )
                    }
                    if (noteId != 0L) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Rounded.DeleteOutline, contentDescription = null, tint = VaultTextFaint)
                        }
                    }
                    // ✅ FIX 3: Check saves ONCE then goes back
                    IconButton(onClick = { doSave(); onBack() }) {
                        Icon(Icons.Rounded.Check, contentDescription = null, tint = VaultAccent)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            BasicTextField(
                value         = title,
                onValueChange = { title = it },
                modifier      = Modifier.fillMaxWidth(),
                textStyle     = TextStyle(
                    color      = VaultText,
                    fontSize   = 24.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 30.sp
                ),
                cursorBrush = SolidColor(VaultAccent),
                decorationBox = { innerTextField ->
                    if (title.isEmpty()) {
                        Text(
                            text  = "Title",
                            style = TextStyle(
                                color      = VaultTextFaint,
                                fontSize   = 24.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 30.sp
                            )
                        )
                    }
                    innerTextField()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = VaultDivider.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(16.dp))

            BasicTextField(
                value         = content,
                onValueChange = { content = it },
                modifier      = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                textStyle = TextStyle(
                    color      = VaultText,
                    fontSize   = 16.sp,
                    lineHeight = 26.sp
                ),
                cursorBrush = SolidColor(VaultAccent),
                decorationBox = { innerTextField ->
                    if (content.isEmpty()) {
                        Text(
                            text  = "Start writing...",
                            style = TextStyle(
                                color      = VaultTextFaint,
                                fontSize   = 16.sp,
                                lineHeight = 26.sp
                            )
                        )
                    }
                    innerTextField()
                }
            )

            Column {
                HorizontalDivider(color = VaultDivider.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding        = PaddingValues(horizontal = 4.dp)
                ) {
                    items(NoteColorOptions) { color ->
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (selectedColor == color) 2.dp else 0.dp,
                                    color = VaultAccent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = color }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}