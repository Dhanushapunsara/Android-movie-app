package com.example.movie2.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.movie2.data.MovieRepository
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    movieRepository: MovieRepository,
    onNavigateToSearchMovies: () -> Unit,
    onNavigateToSearchActors: () -> Unit,
    onNavigateToSearchMultipleMovies: () -> Unit, // New navigation callback
    snackbarHostState: SnackbarHostState
) {
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val buttonSize = Modifier
            .width(200.dp)
            .height(50.dp)

        Button(
            onClick = {
                coroutineScope.launch {
                    try {
                        val added = movieRepository.addHardcodedMovies()
                        if (added) {
                            snackbarHostState.showSnackbar("Movies added to database successfully")
                        } else {
                            snackbarHostState.showSnackbar("Movies already exist in database")
                        }
                    } catch (e: Exception) {
                        snackbarHostState.showSnackbar("Error adding movies: ${e.message}")
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black
            ),
            modifier = buttonSize
        ) {
            Text("Add to DB")
        }

        Spacer(modifier = Modifier.height(15.dp))

        Button(
            onClick = onNavigateToSearchMovies,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black
            ),
            modifier = buttonSize
        ) {
            Text("Search for movies")
        }

        Spacer(modifier = Modifier.height(15.dp))

        Button(
            onClick = onNavigateToSearchActors,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black
            ),
            modifier = buttonSize
        ) {
            Text("Search for Actors")
        }

        Spacer(modifier = Modifier.height(15.dp))

        // New button for searching multiple movies
        Button(
            onClick = onNavigateToSearchMultipleMovies,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black
            ),
            modifier = buttonSize
        ) {
            Text("Search Multiple Movies")
        }
    }
}
