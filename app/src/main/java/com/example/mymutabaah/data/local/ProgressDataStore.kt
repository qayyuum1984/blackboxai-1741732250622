package com.example.mymutabaah.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.mymutabaah.data.model.DailyProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ProgressDataStore(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "progress")
        private val DATE_KEY = stringPreferencesKey("date")
        private val FAJR_KEY = booleanPreferencesKey("fajr")
        private val DHUHR_KEY = booleanPreferencesKey("dhuhr")
        private val ASR_KEY = booleanPreferencesKey("asr")
        private val MAGHRIB_KEY = booleanPreferencesKey("maghrib")
        private val ISHA_KEY = booleanPreferencesKey("isha")
        private val QURAN_KEY = booleanPreferencesKey("quran")
        private val CHARITY_KEY = booleanPreferencesKey("charity")
        
        private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    }

    val dailyProgress: Flow<DailyProgress> = context.dataStore.data.map { preferences ->
        val dateStr = preferences[DATE_KEY] ?: LocalDate.now().format(dateFormatter)
        val date = LocalDate.parse(dateStr, dateFormatter)

        DailyProgress(
            date = date,
            prayers = DailyProgress.Prayers(
                fajr = preferences[FAJR_KEY] ?: false,
                dhuhr = preferences[DHUHR_KEY] ?: false,
                asr = preferences[ASR_KEY] ?: false,
                maghrib = preferences[MAGHRIB_KEY] ?: false,
                isha = preferences[ISHA_KEY] ?: false
            ),
            quranRecited = preferences[QURAN_KEY] ?: false,
            charityGiven = preferences[CHARITY_KEY] ?: false
        )
    }

    suspend fun updateProgress(progress: DailyProgress) {
        context.dataStore.edit { preferences ->
            preferences[DATE_KEY] = progress.date.format(dateFormatter)
            preferences[FAJR_KEY] = progress.prayers.fajr
            preferences[DHUHR_KEY] = progress.prayers.dhuhr
            preferences[ASR_KEY] = progress.prayers.asr
            preferences[MAGHRIB_KEY] = progress.prayers.maghrib
            preferences[ISHA_KEY] = progress.prayers.isha
            preferences[QURAN_KEY] = progress.quranRecited
            preferences[CHARITY_KEY] = progress.charityGiven
        }
    }

    suspend fun clearProgress() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    // Helper function to get historical data (last 7 days)
    suspend fun getWeeklyProgress(): List<DailyProgress> {
        // In a real app, you'd store historical data in a different way
        // This is just a placeholder that returns the current day's progress
        return listOf(
            context.dataStore.data.map { preferences ->
                val dateStr = preferences[DATE_KEY] ?: LocalDate.now().format(dateFormatter)
                val date = LocalDate.parse(dateStr, dateFormatter)

                DailyProgress(
                    date = date,
                    prayers = DailyProgress.Prayers(
                        fajr = preferences[FAJR_KEY] ?: false,
                        dhuhr = preferences[DHUHR_KEY] ?: false,
                        asr = preferences[ASR_KEY] ?: false,
                        maghrib = preferences[MAGHRIB_KEY] ?: false,
                        isha = preferences[ISHA_KEY] ?: false
                    ),
                    quranRecited = preferences[QURAN_KEY] ?: false,
                    charityGiven = preferences[CHARITY_KEY] ?: false
                )
            }.first()
        )
    }
}
