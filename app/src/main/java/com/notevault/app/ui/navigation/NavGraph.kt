package com.notevault.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.notevault.app.ui.screens.AiChatScreen
import com.notevault.app.ui.screens.ArchiveScreen
import com.notevault.app.ui.screens.EditorScreen
import com.notevault.app.ui.screens.HomeScreen
import com.notevault.app.ui.screens.SettingsScreen
import com.notevault.app.ui.screens.TrashScreen
import com.notevault.app.viewmodel.NoteViewModel

@Composable
fun NoteVaultNavGraph(viewModel: NoteViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                viewModel  = viewModel,
                onNew      = { navController.navigate("editor/0") },
                onOpen     = { id -> navController.navigate("editor/$id") },
                onTrash    = { navController.navigate("trash") },
                onArchive  = { navController.navigate("archive") },
                onSettings = { navController.navigate("settings") },
                onAi       = { navController.navigate("ai_chat") }
            )
        }
        composable(
            route = "editor/{noteId}",
            arguments = listOf(
                navArgument("noteId") {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getLong("noteId") ?: 0L
            EditorScreen(
                viewModel = viewModel,
                noteId    = noteId,
                onBack    = { navController.popBackStack() }
            )
        }
        composable("trash") {
            TrashScreen(
                viewModel = viewModel,
                onBack    = { navController.popBackStack() }
            )
        }
        composable("archive") {
            ArchiveScreen(
                viewModel = viewModel,
                onBack    = { navController.popBackStack() }
            )
        }
        composable("settings") {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
        composable("ai_chat") {
            AiChatScreen(onBack = { navController.popBackStack() })
        }
    }
}
