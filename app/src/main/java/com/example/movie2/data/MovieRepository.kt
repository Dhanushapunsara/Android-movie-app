package com.example.movie2.data

import android.content.Context
import android.util.Log
import com.example.movie2.data.api.MovieApiService
import com.example.movie2.data.api.MultipleMovieSearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MovieRepository(context: Context) {
    private val movieDao = MovieDatabase.getDatabase(context).movieDao()
    private val apiService = MovieApiService()

    suspend fun insertMovie(movie: Movie) {
        withContext(Dispatchers.IO) {
            try {
                Log.d("MovieRepository", "Inserting movie: ${movie.title}")
                movieDao.insertMovie(movie)
                Log.d("MovieRepository", "Movie inserted successfully")
            } catch (e: Exception) {
                Log.e("MovieRepository", "Error inserting movie", e)
                throw e
            }
        }
    }

    suspend fun insertMovies(movies: List<Movie>) {
        withContext(Dispatchers.IO) {
            try {
                Log.d("MovieRepository", "Inserting ${movies.size} movies")
                movieDao.insertMovies(movies)
                Log.d("MovieRepository", "Movies inserted successfully")
            } catch (e: Exception) {
                Log.e("MovieRepository", "Error inserting movies", e)
                throw e
            }
        }
    }

    suspend fun searchMoviesByTitle(title: String): List<Movie> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("MovieRepository", "Searching movies by title: $title")
                val results = movieDao.searchMoviesByTitle(title)
                Log.d("MovieRepository", "Found ${results.size} movies by title")
                results
            } catch (e: Exception) {
                Log.e("MovieRepository", "Error searching movies by title", e)
                throw e
            }
        }
    }

    suspend fun searchMoviesByActor(actorName: String): List<Movie> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("MovieRepository", "Searching movies by actor: $actorName")
                val results = movieDao.searchMoviesByActor(actorName)
                Log.d("MovieRepository", "Found ${results.size} movies by actor")
                results
            } catch (e: Exception) {
                Log.e("MovieRepository", "Error searching movies by actor", e)
                throw e
            }
        }
    }

    suspend fun getMovieCount(): Int {
        return withContext(Dispatchers.IO) {
            movieDao.getMovieCount()
        }
    }

    suspend fun clearDatabase() {
        withContext(Dispatchers.IO) {
            try {
                Log.d("MovieRepository", "Clearing database")
                movieDao.clearAllMovies()
                Log.d("MovieRepository", "Database cleared successfully")
            } catch (e: Exception) {
                Log.e("MovieRepository", "Error clearing database", e)
                throw e
            }
        }
    }

    suspend fun searchMovieFromApi(title: String) = apiService.searchMovieByTitle(title)

    // New method to search for multiple movies from the API
    suspend fun searchMultipleMoviesFromApi(searchTerm: String): Result<List<MultipleMovieSearchResult>> {
        return try {
            Log.d("MovieRepository", "Searching multiple movies with term: $searchTerm")
            val result = apiService.searchMultipleMovies(searchTerm)
            result.onSuccess { movies ->
                Log.d("MovieRepository", "Found ${movies.size} movies from API")
            }.onFailure { error ->
                Log.e("MovieRepository", "Error searching multiple movies", error)
            }
            result
        } catch (e: Exception) {
            Log.e("MovieRepository", "Exception searching multiple movies", e)
            Result.failure(e)
        }
    }

    suspend fun addHardcodedMovies(): Boolean {
        // Check if movies are already in the database
        val count = getMovieCount()
        if (count > 0) {
            Log.d("MovieRepository", "Movies already exist in database, count: $count")
            return false // Movies already exist, don't add again
        }

        Log.d("MovieRepository", "Adding hardcoded movies to database")
        val movies = listOf(
            Movie(
                title = "The Shawshank Redemption",
                year = "1994",
                rated = "R",
                released = "14 Oct 1994",
                runtime = "142 min",
                genre = "Drama",
                director = "Frank Darabont",
                writer = "Stephen King, Frank Darabont",
                actors = "Tim Robbins, Morgan Freeman, Bob Gunton",
                plot = "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency."
            ),
            Movie(
                title = "The Matrix",
                year = "1999",
                rated = "R",
                released = "31 Mar 1999",
                runtime = "136 min",
                genre = "Action, Sci-Fi",
                director = "Lana Wachowski, Lilly Wachowski",
                writer = "Lilly Wachowski, Lana Wachowski",
                actors = "Keanu Reeves, Laurence Fishburne, Carrie-Anne Moss",
                plot = "When a beautiful stranger leads computer hacker Neo to a forbidding underworld, he discovers the shocking truth--the life he knows is the elaborate deception of an evil cyber-intelligence."
            )
        )
        insertMovies(movies)
        return true // Movies were added
    }
}
