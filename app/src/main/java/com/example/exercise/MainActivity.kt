package com.example.exercise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.exercise.data.NewsRepository
import com.example.exercise.ui.NewsNavigation
import com.example.exercise.ui.NewsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NewsReaderApp()
        }
    }
}

@Composable
fun NewsReaderApp() {
    val context = LocalContext.current
    val repository = NewsRepository(context)
    val viewModel = NewsViewModel(repository)

    MaterialTheme {
        Surface {
            NewsNavigation(viewModel)
        }
    }
}