package com.notevault.app.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.notevault.app.data.SettingsDataStore
import com.notevault.app.ui.theme.VaultAccent
import com.notevault.app.ui.theme.VaultBg
import com.notevault.app.ui.theme.VaultCard
import com.notevault.app.ui.theme.VaultDivider
import com.notevault.app.ui.theme.VaultSurface
import com.notevault.app.ui.theme.VaultText
import com.notevault.app.ui.theme.VaultTextFaint
import com.notevault.app.ui.theme.VaultTextMuted
import com.notevault.app.viewmodel.SettingsViewModel
import com.notevault.app.viewmodel.SettingsViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

data class AiMessage(val text: String, val isUser: Boolean)

suspend fun callGemini(apiKey: String, history: List<AiMessage>, userInput: String): String {
    return withContext(Dispatchers.IO) {
        try {
            val url = URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("x-goog-api-key", apiKey)
            conn.doOutput = true
            conn.connectTimeout = 15000
            conn.readTimeout = 30000

            val contents = JSONArray()
            history.forEach { msg ->
                val part = JSONObject().put("text", msg.text)
                val parts = JSONArray().put(part)
                val role = if (msg.isUser) "user" else "model"
                contents.put(JSONObject().put("role", role).put("parts", parts))
            }
            val newPart = JSONObject().put("text", userInput)
            val newParts = JSONArray().put(newPart)
            contents.put(JSONObject().put("role", "user").put("parts", newParts))

            val body = JSONObject().put("contents", contents).toString()
            OutputStreamWriter(conn.outputStream).use { it.write(body) }

            val responseCode = conn.responseCode
            if (responseCode == 200) {
                val response = conn.inputStream.bufferedReader().readText()
                val json = JSONObject(response)
                json.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text")
            } else if (responseCode == 429) {
                "⚠️ Rate limit reached. Try again in a few minutes."
            } else if (responseCode == 400) {
                "⚠️ Invalid API key. Please re-enter your key (tap key icon)."
            } else if (responseCode == 403) {
                "⚠️ API key doesn't have permission. Check Google AI Studio."
            } else if (responseCode == 404) {
                "⚠️ Model not found. Make sure your key is from aistudio.google.com"
            } else {
                "Error $responseCode. Please try again."
            }
        } catch (e: Exception) {
            "Connection failed: ${e.message}"
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(SettingsDataStore(context))
    )

    val apiKey by settingsViewModel.apiKey.collectAsState()
    var input by remember { mutableStateOf("") }
    var showKeyField by remember { mutableStateOf(apiKey.isEmpty()) }
    var isLoading by remember { mutableStateOf(false) }
    val messages = remember { mutableStateListOf<AiMessage>() }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = VaultBg,
        topBar = {
            TopAppBar(
                title = { Text("AI Assistant", color = VaultText) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Rounded.ArrowBack,
                            contentDescription = null,
                            tint = VaultTextMuted
                        )
                    }
                },
                modifier = Modifier.statusBarsPadding(),
                actions = {
                    if (showKeyField && apiKey.isNotEmpty()) {
                        IconButton(onClick = { settingsViewModel.clearApiKey() }) {
                            Icon(
                                imageVector = Icons.Rounded.Clear,
                                contentDescription = "Clear API key",
                                tint = VaultTextMuted
                            )
                        }
                    }
                    IconButton(onClick = { showKeyField = !showKeyField }) {
                        Icon(
                            imageVector = Icons.Rounded.Key,
                            contentDescription = null,
                            tint = if (apiKey.isNotEmpty()) VaultAccent else VaultTextFaint
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
                .imePadding()        // ✅ pushes content above keyboard like WhatsApp
        ) {
            if (showKeyField) {
                OutlinedTextField(
                    value = apiKey,
                    onValueChange = {
                        settingsViewModel.setApiKey(it)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    placeholder = { Text("Paste Gemini API key", color = VaultTextFaint) },
                    leadingIcon = {
                        Icon(Icons.Rounded.Key, contentDescription = null, tint = VaultTextMuted)
                    },
                    supportingText = {
                        Text(
                            if (apiKey.isNotEmpty()) "✓ Key saved on device" else "Get key from aistudio.google.com",
                            color = if (apiKey.isNotEmpty()) VaultAccent else VaultTextFaint
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor      = VaultAccent,
                        unfocusedBorderColor    = VaultDivider,
                        focusedContainerColor   = VaultSurface,
                        unfocusedContainerColor = VaultSurface,
                        focusedTextColor        = VaultText,
                        unfocusedTextColor      = VaultText,
                        cursorColor             = VaultAccent
                    ),
                    singleLine = true
                )
            }

            if (messages.isEmpty() && !isLoading) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint = VaultAccent
                        )
                        Text(
                            "AI Assistant",
                            color = VaultText,
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = if (apiKey.isEmpty())
                                "Tap the key icon ↗ to add your Gemini API key"
                            else
                                "Gemini 2.5 Flash ready ✓ Ask anything",
                            color = VaultTextMuted,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(messages) { msg ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .widthIn(max = 280.dp)
                                    .background(
                                        color = if (msg.isUser) VaultAccent else VaultCard,
                                        shape = RoundedCornerShape(
                                            topStart    = 18.dp,
                                            topEnd      = 18.dp,
                                            bottomStart = if (msg.isUser) 18.dp else 4.dp,
                                            bottomEnd   = if (msg.isUser) 4.dp else 18.dp
                                        )
                                    )
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text  = msg.text,
                                    color = if (msg.isUser) VaultText else VaultTextMuted,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                    if (isLoading) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            VaultCard,
                                            RoundedCornerShape(18.dp, 18.dp, 18.dp, 4.dp)
                                        )
                                        .padding(horizontal = 20.dp, vertical = 14.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        color = VaultAccent,
                                        strokeWidth = 2.dp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ✅ Typing bar — always visible above keyboard
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    placeholder = { Text("Ask AI...", color = VaultTextFaint) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor      = VaultAccent,
                        unfocusedBorderColor    = VaultDivider,
                        focusedContainerColor   = VaultSurface,
                        unfocusedContainerColor = VaultSurface,
                        focusedTextColor        = VaultText,
                        unfocusedTextColor      = VaultText,
                        cursorColor             = VaultAccent
                    ),
                    singleLine = true
                )
                FloatingActionButton(
                    onClick = {
                        if (input.isNotBlank() && !isLoading) {
                            if (apiKey.isEmpty()) {
                                showKeyField = true
                                return@FloatingActionButton
                            }
                            val userMsg = input
                            input = ""
                            messages.add(AiMessage(userMsg, true))
                            isLoading = true
                            scope.launch {
                                val reply = callGemini(apiKey, messages.toList(), userMsg)
                                messages.add(AiMessage(reply, false))
                                isLoading = false
                                listState.animateScrollToItem(messages.size - 1)
                            }
                        }
                    },
                    containerColor = if (isLoading) VaultSurface else VaultAccent,
                    contentColor   = VaultText,
                    modifier       = Modifier.size(50.dp),
                    shape          = RoundedCornerShape(14.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = VaultAccent,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            Icons.Rounded.Send,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}