package com.notevault.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary             = VaultAccent,
    onPrimary           = VaultText,
    primaryContainer    = VaultAccentDim,
    onPrimaryContainer  = VaultAccentLight,
    secondary           = VaultMint,
    onSecondary         = VaultBg,
    secondaryContainer  = VaultMintDim,
    onSecondaryContainer = VaultText,
    background          = VaultBg,
    onBackground        = VaultText,
    surface             = VaultSurface,
    onSurface           = VaultText,
    surfaceVariant      = VaultCard,
    onSurfaceVariant    = VaultTextMuted,
    outline             = VaultDivider
)

private val LightColorScheme = lightColorScheme(
    primary             = LightVaultAccent,
    onPrimary           = LightVaultText,
    primaryContainer    = LightVaultAccentDim,
    onPrimaryContainer  = LightVaultAccentLight,
    secondary           = LightVaultMint,
    onSecondary         = LightVaultBg,
    secondaryContainer  = LightVaultMintDim,
    onSecondaryContainer = LightVaultText,
    background          = LightVaultBg,
    onBackground        = LightVaultText,
    surface             = LightVaultSurface,
    onSurface           = LightVaultText,
    surfaceVariant      = LightVaultCard,
    onSurfaceVariant    = LightVaultTextMuted,
    outline             = LightVaultDivider
)

@Composable
fun NoteVaultTheme(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()
    val colorScheme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (isDarkTheme) {
            dynamicDarkColorScheme(context)
        } else {
            dynamicLightColorScheme(context)
        }
    } else {
        if (isDarkTheme) {
            DarkColorScheme
        } else {
            LightColorScheme
        }
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDarkTheme
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
