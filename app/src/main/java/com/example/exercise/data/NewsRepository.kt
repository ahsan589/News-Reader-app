package com.example.exercise.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore by preferencesDataStore(name = "favorites")

class NewsRepository(private val context: Context) {
    private val apiService = ApiClient.retrofitService

    suspend fun getNews(): List<NewsItem> {
        val news = apiService.getNews()
        return news.map { item ->
            item.copy(isFavorite = isFavorite(item.id))
        }
    }

    suspend fun getNewsItem(id: Int): NewsItem {
        val item = apiService.getNewsItem(id)
        return item.copy(isFavorite = isFavorite(item.id))
    }

    private suspend fun isFavorite(id: Int): Boolean {
        val key = booleanPreferencesKey(id.toString())
        val preferences = context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { it[key] ?: false }

        return preferences.first()
    }

    suspend fun toggleFavorite(id: Int) {
        val key = booleanPreferencesKey(id.toString())
        context.dataStore.edit { preferences ->
            val current = preferences[key] ?: false
            preferences[key] = !current
        }
    }

    // Corrected version that doesn't need getFavorites()
    suspend fun getFavoriteNewsItems(): Flow<List<NewsItem>> {
        return context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                val favoriteIds = preferences.asMap()
                    .filter { it.value == true }
                    .map { it.key.name.toInt() }

                // Get fresh news list and filter
                getNews().filter { it.id in favoriteIds }
            }
    }
}