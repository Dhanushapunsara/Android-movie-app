package com.example.movie2

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.movie2.data.MovieRepository
import com.example.movie2.screens.HomeScreen
import com.example.movie2.screens.SearchActorsScreen
import com.example.movie2.screens.SearchMoviesScreen
import com.example.movie2.screens.SearchMultipleMoviesScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var movieRepository: MovieRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize the repository
        movieRepository = MovieRepository(applicationContext)

        // Clear the database when the app starts
        val coroutineScope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main)
        coroutineScope.launch {
            try {
                Log.d("MainActivity", "Clearing database on app start")
                movieRepository.clearDatabase()
                Log.d("MainActivity", "Database cleared successfully")
            } catch (e: Exception) {
                Log.e("MainActivity", "Error clearing database", e)
            }
        }

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainApp(movieRepository)
                }
            }
        }
    }
}

@Composable
fun MainApp(movieRepository: MovieRepository) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var currentScreen by rememberSaveable { mutableStateOf("home") }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        when (currentScreen) {
            "home" -> {
                HomeScreen(
                    modifier = Modifier.padding(innerPadding),
                    movieRepository = movieRepository,
                    onNavigateToSearchMovies = { currentScreen = "searchMovies" },
                    onNavigateToSearchActors = { currentScreen = "searchActors" },
                    onNavigateToSearchMultipleMovies = { currentScreen = "searchMultipleMovies" }, // New navigation callback
                    snackbarHostState = snackbarHostState
                )
            }
            "searchMovies" -> {
                SearchMoviesScreen(
                    movieRepository = movieRepository,
                    onNavigateBack = { currentScreen = "home" }
                )
            }
            "searchActors" -> {
                SearchActorsScreen(
                    movieRepository = movieRepository,
                    onNavigateBack = { currentScreen = "home" }
                )
            }
            "searchMultipleMovies" -> {
                SearchMultipleMoviesScreen(
                    movieRepository = movieRepository,
                    onNavigateBack = { currentScreen = "home" }
                )
            }
        }
    }
}
