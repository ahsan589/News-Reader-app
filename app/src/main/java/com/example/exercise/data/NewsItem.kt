package com.example.exercise.data


data class NewsItem(
    val id: Int,
    val title: String,
    val body: String,
    val userId: Int,
    val isFavorite: Boolean = false
)