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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchActorsScreen(
    movieRepository: MovieRepository,
    onNavigateBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var searchResults by remember { mutableStateOf<List<Movie>>(emptyList()) }
    var hasSearched by remember { mutableStateOf(false) }

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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Search field and button
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    // Reset search results when query changes
                    if (hasSearched) {
                        searchResults = emptyList()
                        hasSearched = false
                    }
                },
                label = { Text("Enter actor name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (searchQuery.isNotBlank()) {
                        coroutineScope.launch {
                            isLoading = true
                            try {
                                // Search for movies by actor name
                                val results = movieRepository.searchMoviesByActor(searchQuery)
                                searchResults = results
                                hasSearched = true

                                if (results.isEmpty()) {
                                    snackbarHostState.showSnackbar("No movies found with actor: $searchQuery")
                                }
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Error searching: ${e.message}")
                            } finally {
                                isLoading = false
                            }
                        }
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Please enter an actor name")
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Search")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Loading indicator
            if (isLoading) {
                CircularProgressIndicator()
            }

            // Search results
            if (hasSearched && !isLoading) {
                if (searchResults.isEmpty()) {
                    Text(
                        text = "No movies found with actor: $searchQuery",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    Text(
                        text = "Found ${searchResults.size} movie(s) with actor: $searchQuery",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    LazyColumn {
                        items(searchResults) { movie ->
                            MovieCard(movie = movie)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MovieCard(movie: Movie) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row {
                Text(
                    text = "Year: ",
                    fontWeight = FontWeight.Bold
                )
                Text(text = movie.year)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row {
                Text(
                    text = "Genre: ",
                    fontWeight = FontWeight.Bold
                )
                Text(text = movie.genre)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Column {
                Text(
                    text = "Actors: ",
                    fontWeight = FontWeight.Bold
                )
                Text(text = movie.actors)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Column {
                Text(
                    text = "Plot: ",
                    fontWeight = FontWeight.Bold
                )
                Text(text = movie.plot)
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}
