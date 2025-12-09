package com.example.androiddevelopers.ui.events

enum class HistoricalPeriod(
    val displayName: String,
    val startYear: Int,
    val endYear: Int,
    val colorResId: Int
) {
    ANCIENT(
        "Edad Antigua",
        -3000,
        476,
        com.example.androiddevelopers.R.color.era_ancient
    ),
    MIDDLE_AGES(
        "Edad Media",
        476,
        1492,
        com.example.androiddevelopers.R.color.era_middle_ages
    ),
    MODERN(
        "Edad Moderna",
        1492,
        1789,
        com.example.androiddevelopers.R.color.era_modern
    ),
    CONTEMPORARY(
        "Historia Contemporánea",
        1789,
        2025,
        com.example.androiddevelopers.R.color.era_contemporary
    );

}