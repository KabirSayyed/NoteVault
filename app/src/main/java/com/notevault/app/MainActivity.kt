package com.notevault.app

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.notevault.app.data.FpsMode
import com.notevault.app.data.SettingsDataStore
import com.notevault.app.ui.navigation.NoteVaultNavGraph
import com.notevault.app.ui.theme.NoteVaultTheme
import com.notevault.app.viewmodel.NoteViewModel
import com.notevault.app.viewmodel.NoteViewModelFactory

class MainActivity : ComponentActivity() {
    private val settingsDataStore by lazy { SettingsDataStore(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Enable immersive full screen mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let { controller ->
                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
            )
        }
        
        val app = application as NoteVaultApp
        setContent {
            NoteVaultTheme {
                val vm: NoteViewModel = viewModel(
                    factory = NoteViewModelFactory(app.repository)
                )
                FpsSettingsController(settingsDataStore = settingsDataStore) {
                    NoteVaultNavGraph(viewModel = vm)
                }
            }
        }
    }
}

@Composable
fun FpsSettingsController(settingsDataStore: SettingsDataStore, content: @Composable () -> Unit) {
    val fpsMode = settingsDataStore.fpsModeFlow.collectAsState(FpsMode.FPS_60)
    val activity = LocalContext.current as? ComponentActivity

    LaunchedEffect(fpsMode.value) {
        activity?.let { applyFpsMode(it, fpsMode.value) }
    }

    Box {
        content()
    }
}

private fun applyFpsMode(activity: ComponentActivity, fpsMode: FpsMode) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val preferredRefreshRate = when (fpsMode) {
            FpsMode.LOW_FPS -> 30f
            FpsMode.FPS_60 -> 60f
            FpsMode.FPS_90 -> 90f
        }
        
        try {
            activity.window.attributes = activity.window.attributes.apply {
                this.preferredRefreshRate = preferredRefreshRate
            }
        } catch (e: Exception) {
            // Fallback if setPreferredRefreshRate not available
            e.printStackTrace()
        }
    }
}
