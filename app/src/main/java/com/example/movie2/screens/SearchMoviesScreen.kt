package com.example.movie2.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.movie2.data.Movie
import com.example.movie2.data.MovieRepository
import com.example.movie2.data.api.MovieApiResponse
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchMoviesScreen(
    movieRepository: MovieRepository,
    onNavigateBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var movieResult by remember { mutableStateOf<MovieApiResponse?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var savedToDb by remember { mutableStateOf(false) }

    // Handle back button press
    BackHandler {
        onNavigateBack()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Search field and button
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    // Reset states when query changes
                    movieResult = null
                    errorMessage = null
                    savedToDb = false
                },
                label = { Text("Enter movie title") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        if (searchQuery.isNotBlank()) {
                            coroutineScope.launch {
                                isLoading = true
                                errorMessage = null
                                movieResult = null
                                savedToDb = false

                                val result = movieRepository.searchMovieFromApi(searchQuery)
                                isLoading = false

                                result.fold(
                                    onSuccess = { response ->
                                        movieResult = response
                                    },
                                    onFailure = { error ->
                                        errorMessage = error.message ?: "Unknown error occurred"
                                        snackbarHostState.showSnackbar("Error: $errorMessage")
                                    }
                                )
                            }
                        } else {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Please enter a movie title")
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black
                    )
                ) {
                    Text("Retrieve Movie")
                }

                Button(
                    onClick = {
                        movieResult?.let { movie ->
                            coroutineScope.launch {
                                try {
                                    // Convert API response to Movie entity and save it
                                    val movieEntity = Movie(
                                        title = movie.Title,
                                        year = movie.Year,
                                        rated = movie.Rated,
                                        released = movie.Released,
                                        runtime = movie.Runtime,
                                        genre = movie.Genre,
                                        director = movie.Director,
                                        writer = movie.Writer,
                                        actors = movie.Actors,
                                        plot = movie.Plot
                                    )

                                    // Insert the movie into the database
                                    movieRepository.insertMovie(movieEntity)
                                    savedToDb = true
                                    snackbarHostState.showSnackbar("Movie saved to database successfully")
                                } catch (e: Exception) {
                                    snackbarHostState.showSnackbar("Error saving movie: ${e.message}")
                                }
                            }
                        } ?: run {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("No movie to save")
                            }
                        }
                    },
                    enabled = movieResult != null && !savedToDb,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        disabledContainerColor = Color.Gray
                    )
                ) {
                    Text("Save movie to Database")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Loading indicator
            if (isLoading) {
                CircularProgressIndicator()
            }

            // Error message
            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Movie result
            movieResult?.let { movie ->
                MovieDetailsCard(movie = movie)
            }
        }
    }
}

@Composable
fun MovieDetailsCard(movie: MovieApiResponse) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            MovieDetailItem("Title", movie.Title)
            MovieDetailItem("Year", movie.Year)
            MovieDetailItem("Rated", movie.Rated)
            MovieDetailItem("Released", movie.Released)
            MovieDetailItem("Runtime", movie.Runtime)
            MovieDetailItem("Genre", movie.Genre)
            MovieDetailItem("Director", movie.Director)
            MovieDetailItem("Writer", movie.Writer)
            MovieDetailItem("Actors", movie.Actors)
            MovieDetailItem("Plot", movie.Plot)
        }
    }
}

@Composable
fun MovieDetailItem(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "$label:",
            fontWeight = FontWeight.Bold
        )
        Text(text = value)
        Divider(modifier = Modifier.padding(vertical = 8.dp))
    }
}
