package com.example.exercise.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exercise.data.NewsItem
import com.example.exercise.data.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NewsViewModel(private val repository: NewsRepository) : ViewModel() {
    var newsList by mutableStateOf<List<NewsItem>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    private val _selectedNewsItem = MutableStateFlow<NewsItem?>(null)
    val selectedNewsItem: StateFlow<NewsItem?> = _selectedNewsItem.asStateFlow()

    private val _favoriteNews = MutableStateFlow<List<NewsItem>>(emptyList())
    val favoriteNews: StateFlow<List<NewsItem>> = _favoriteNews.asStateFlow()

    init {
        loadNews()
        loadFavorites()
    }

    fun loadNews() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                newsList = repository.getNews()
            } catch (e: Exception) {
                errorMessage = e.message ?: "Unknown error"
            } finally {
                isLoading = false
            }
        }
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            repository.getFavoriteNewsItems().collect { favorites ->
                _favoriteNews.value = favorites
            }
        }
    }

    fun selectNewsItem(id: Int) {
        viewModelScope.launch {
            _selectedNewsItem.value = repository.getNewsItem(id)
        }
    }

    fun toggleFavorite(id: Int) {
        viewModelScope.launch {
            repository.toggleFavorite(id)
            // Refresh both lists
            newsList = newsList.map { item ->
                if (item.id == id) item.copy(isFavorite = !item.isFavorite) else item
            }
            // Update selected item if it's the one being toggled
            _selectedNewsItem.value?.let { current ->
                if (current.id == id) {
                    _selectedNewsItem.value = current.copy(isFavorite = !current.isFavorite)
                }
            }
            // Refresh favorites
            loadFavorites()
        }
    }
}