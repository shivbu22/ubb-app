package com.ubb.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey val id: Int = 1, // Singleton row
    val totalSessions: Int = 0,
    val totalSecondsListened: Long = 0L,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val lastSessionTimestamp: Long = 0L
)
