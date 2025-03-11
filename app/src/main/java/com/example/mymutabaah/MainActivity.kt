package com.example.mymutabaah

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.InsertChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mymutabaah.ui.screens.DailyTrackingScreen
import com.example.mymutabaah.ui.screens.DashboardScreen
import com.example.mymutabaah.ui.theme.MyMutabaahTheme
import com.example.mymutabaah.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyMutabaahTheme {
                MyMutabaahApp(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyMutabaahApp(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination
    val currentScreen = remember(currentDestination) {
        currentDestination?.route ?: Screen.DailyTracking.route
    }

    // SnackBar for error messages
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState by viewModel.uiState.collectAsState()

    // Show error message in SnackBar
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            // Clear the error message after showing it
            viewModel.clearErrorMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        when (currentScreen) {
                            Screen.DailyTracking.route -> "My Mutabaah"
                            Screen.Dashboard.route -> "Dashboard"
                            else -> "My Mutabaah"
                        },
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.DateRange, contentDescription = "Daily Tracking") },
                    label = { Text("Daily") },
                    selected = currentScreen == Screen.DailyTracking.route,
                    onClick = {
                        navController.navigate(Screen.DailyTracking.route) {
                            popUpTo(Screen.DailyTracking.route) { inclusive = true }
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.InsertChart, contentDescription = "Dashboard") },
                    label = { Text("Dashboard") },
                    selected = currentScreen == Screen.Dashboard.route,
                    onClick = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.DailyTracking.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.DailyTracking.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.DailyTracking.route) {
                DailyTrackingScreen(
                    viewModel = viewModel
                )
            }
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    viewModel = viewModel
                )
            }
        }
    }
}

sealed class Screen(val route: String) {
    object DailyTracking : Screen("daily_tracking")
    object Dashboard : Screen("dashboard")
}
