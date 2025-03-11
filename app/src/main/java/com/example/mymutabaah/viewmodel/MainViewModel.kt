package com.example.mymutabaah.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymutabaah.data.local.ProgressDataStore
import com.example.mymutabaah.data.model.DailyProgress
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val progressDataStore = ProgressDataStore(application)

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _weeklyProgress = MutableStateFlow<List<DailyProgress>>(emptyList())
    val weeklyProgress: StateFlow<List<DailyProgress>> = _weeklyProgress.asStateFlow()

    init {
        // Load initial data
        viewModelScope.launch {
            progressDataStore.dailyProgress.collect { progress ->
                _uiState.update { currentState ->
                    currentState.copy(
                        dailyProgress = progress,
                        isLoading = false
                    )
                }
            }
        }

        // Load weekly progress
        loadWeeklyProgress()
    }

    fun updatePrayer(prayer: Prayer, completed: Boolean) {
        val currentProgress = _uiState.value.dailyProgress
        val updatedPrayers = when (prayer) {
            Prayer.FAJR -> currentProgress.prayers.copy(fajr = completed)
            Prayer.DHUHR -> currentProgress.prayers.copy(dhuhr = completed)
            Prayer.ASR -> currentProgress.prayers.copy(asr = completed)
            Prayer.MAGHRIB -> currentProgress.prayers.copy(maghrib = completed)
            Prayer.ISHA -> currentProgress.prayers.copy(isha = completed)
        }

        val updatedProgress = currentProgress.copy(prayers = updatedPrayers)
        updateProgress(updatedProgress)
    }

    fun updateQuranRecitation(completed: Boolean) {
        val updatedProgress = _uiState.value.dailyProgress.copy(quranRecited = completed)
        updateProgress(updatedProgress)
    }

    fun updateCharity(completed: Boolean) {
        val updatedProgress = _uiState.value.dailyProgress.copy(charityGiven = completed)
        updateProgress(updatedProgress)
    }

    private fun updateProgress(progress: DailyProgress) {
        viewModelScope.launch {
            try {
                progressDataStore.updateProgress(progress)
                _uiState.update { currentState ->
                    currentState.copy(
                        dailyProgress = progress,
                        errorMessage = null
                    )
                }
                // Refresh weekly progress after update
                loadWeeklyProgress()
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        errorMessage = "Failed to save progress: ${e.message}"
                    )
                }
            }
        }
    }

    private fun loadWeeklyProgress() {
        viewModelScope.launch {
            try {
                val weeklyData = progressDataStore.getWeeklyProgress()
                _weeklyProgress.value = weeklyData
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        errorMessage = "Failed to load weekly progress: ${e.message}"
                    )
                }
            }
        }
    }

    fun clearErrorMessage() {
        _uiState.update { currentState ->
            currentState.copy(errorMessage = null)
        }
    }

    data class UiState(
        val dailyProgress: DailyProgress = DailyProgress(),
        val isLoading: Boolean = true,
        val errorMessage: String? = null
    )

    enum class Prayer {
        FAJR, DHUHR, ASR, MAGHRIB, ISHA
    }
}
