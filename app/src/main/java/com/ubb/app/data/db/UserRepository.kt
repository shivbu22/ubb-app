package com.ubb.app.data.db

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class UserRepository(private val userDao: UserStatsDao) {

    val userStats: Flow<UserStatsEntity> = userDao.getUserStats().map {
        it ?: UserStatsEntity()
    }

    /**
     * Records a completed listening session, updating totals and
     * calculating streaks using calendar-day boundaries (timezone-aware).
     *
     * Streak rules:
     * - First ever session → streak = 1
     * - Session on the same calendar day → streak unchanged
     * - Session on the next calendar day → streak + 1
     * - Session after a gap of 2+ days → streak resets to 1
     */
    suspend fun recordSession(durationSeconds: Long) {
        val currentStats = userDao.getUserStatsSync() ?: UserStatsEntity()
        val now = System.currentTimeMillis()
        val userZone = ZoneId.systemDefault()

        val todayDate = Instant.ofEpochMilli(now)
            .atZone(userZone)
            .toLocalDate()

        val newStreak: Int
        if (currentStats.lastSessionTimestamp == 0L) {
            // First session ever
            newStreak = 1
        } else {
            val lastDate = Instant.ofEpochMilli(currentStats.lastSessionTimestamp)
                .atZone(userZone)
                .toLocalDate()

            val daysBetween = java.time.temporal.ChronoUnit.DAYS.between(lastDate, todayDate)

            newStreak = when {
                daysBetween == 0L -> currentStats.currentStreak  // Same day
                daysBetween == 1L -> currentStats.currentStreak + 1  // Consecutive day
                else -> 1  // Streak broken (gap ≥ 2 days)
            }
        }

        // Track best streak ever achieved
        val bestStreak = maxOf(newStreak, currentStats.bestStreak)

        val updatedStats = currentStats.copy(
            totalSessions = currentStats.totalSessions + 1,
            totalSecondsListened = currentStats.totalSecondsListened + durationSeconds,
            currentStreak = newStreak,
            bestStreak = bestStreak,
            lastSessionTimestamp = now
        )
        userDao.insertOrUpdate(updatedStats)
    }
}
