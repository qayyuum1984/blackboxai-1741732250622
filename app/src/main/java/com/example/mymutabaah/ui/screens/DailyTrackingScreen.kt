package com.example.mymutabaah.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mymutabaah.viewmodel.MainViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DailyTrackingScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val progress = uiState.dailyProgress

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Date Display
        val today = LocalDate.now()
        val formattedDate = remember(today) {
            today.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"))
        }
        
        Text(
            text = formattedDate,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Prayers Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Daily Prayers",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                CheckboxItem(
                    text = "Fajr",
                    checked = progress.prayers.fajr,
                    onCheckedChange = { viewModel.updatePrayer(MainViewModel.Prayer.FAJR, it) }
                )
                CheckboxItem(
                    text = "Dhuhr",
                    checked = progress.prayers.dhuhr,
                    onCheckedChange = { viewModel.updatePrayer(MainViewModel.Prayer.DHUHR, it) }
                )
                CheckboxItem(
                    text = "Asr",
                    checked = progress.prayers.asr,
                    onCheckedChange = { viewModel.updatePrayer(MainViewModel.Prayer.ASR, it) }
                )
                CheckboxItem(
                    text = "Maghrib",
                    checked = progress.prayers.maghrib,
                    onCheckedChange = { viewModel.updatePrayer(MainViewModel.Prayer.MAGHRIB, it) }
                )
                CheckboxItem(
                    text = "Isha",
                    checked = progress.prayers.isha,
                    onCheckedChange = { viewModel.updatePrayer(MainViewModel.Prayer.ISHA, it) }
                )
            }
        }

        // Other Activities Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Other Activities",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                CheckboxItem(
                    text = "Quran Recitation",
                    checked = progress.quranRecited,
                    onCheckedChange = { viewModel.updateQuranRecitation(it) }
                )
                CheckboxItem(
                    text = "Charity",
                    checked = progress.charityGiven,
                    onCheckedChange = { viewModel.updateCharity(it) }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Error Message
        uiState.errorMessage?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // Loading Indicator
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
private fun CheckboxItem(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
