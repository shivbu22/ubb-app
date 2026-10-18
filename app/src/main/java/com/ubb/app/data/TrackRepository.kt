package com.ubb.app.data

data class Track(
    val id: Int,
    val title: String,
    val category: String,
    val frequency: String,
    val waveType: String,
    val duration: String,
    val description: String,
    val rawResName: String,
)

object TrackRepository {
    val categories = listOf("All", "Focus", "Sleep", "Meditation", "Stress Relief", "Memory", "Euphoria")

    val tracks = listOf(
        Track(1, "Laser Sharp Focus", "Focus", "14Hz", "Beta Waves", "20:00",
            "Enhance concentration and mental clarity with 14Hz Beta wave entrainment.", "laser_sharp_focus"),
        Track(2, "Sweet Deep Sleep", "Sleep", "2Hz", "Delta Waves", "45:00",
            "Drift into restorative deep sleep with gentle 2Hz Delta frequencies.", "sweet_deep_sleep"),
        Track(3, "Memory Booster", "Memory", "10Hz", "Alpha Waves", "20:00",
            "Strengthen memory recall and retention through Alpha wave stimulation.", "memory_booster"),
        Track(4, "Intelligence Booster", "Focus", "15Hz", "Beta Waves", "30:00",
            "Boost cognitive function and problem-solving with Beta entrainment.", "intelligence_booster"),
        Track(5, "Nirvana Meditation Waves", "Meditation", "6Hz", "Theta Waves", "15:20",
            "Achieve deep meditative states with 6Hz Theta wave immersion.", "nirvana_meditation_waves"),
        Track(6, "Feel Good Euphoria", "Euphoria", "10Hz", "Alpha Waves", "18:00",
            "Elevate your mood and experience natural euphoria with Alpha waves.", "feel_good_euphoria"),
        Track(7, "Ultimate Stress Relief", "Stress Relief", "8Hz", "Alpha Waves", "20:00",
            "Dissolve tension and anxiety with calming 8Hz Alpha frequencies.", "ultimate_stress_relief"),
        Track(8, "Genius Topper", "Focus", "12Hz", "Alpha Waves", "20:00",
            "Unlock peak performance and creative thinking with Alpha entrainment.", "genius_topper"),
        Track(9, "Unity of Hemisphere", "Meditation", "7Hz", "Theta Waves", "24:00",
            "Synchronize left and right brain hemispheres for whole-brain coherence.", "unity_of_hemisphere"),
    )

    fun getByCategory(category: String): List<Track> {
        return if (category == "All") tracks else tracks.filter { it.category == category }
    }

    fun getById(id: Int): Track? = tracks.find { it.id == id }
}
