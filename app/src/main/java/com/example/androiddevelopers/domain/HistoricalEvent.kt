package com.example.androiddevelopers.domain

data class HistoricalEvent(
    val id: Int,
    val title: String,
    val year: String,
    val description: String,
    val detailedDescription: String,
    val imageUrl: String?,
    val articleUrl: String?
)