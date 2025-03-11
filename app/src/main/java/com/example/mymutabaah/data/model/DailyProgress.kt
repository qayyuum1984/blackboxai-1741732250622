package com.example.mymutabaah.data.model

import java.time.LocalDate

data class DailyProgress(
    val date: LocalDate = LocalDate.now(),
    val prayers: Prayers = Prayers(),
    val quranRecited: Boolean = false,
    val charityGiven: Boolean = false
) {
    data class Prayers(
        val fajr: Boolean = false,
        val dhuhr: Boolean = false,
        val asr: Boolean = false,
        val maghrib: Boolean = false,
        val isha: Boolean = false
    ) {
        fun getCompletionPercentage(): Float {
            val completed = listOf(fajr, dhuhr, asr, maghrib, isha).count { it }
            return completed.toFloat() / 5
        }
    }

    fun getOverallCompletionPercentage(): Float {
        val totalActivities = 7 // 5 prayers + Quran + Charity
        val completedPrayers = prayers.getCompletionPercentage() * 5
        val completedOthers = listOf(quranRecited, charityGiven).count { it }
        return (completedPrayers + completedOthers) / totalActivities
    }
}
