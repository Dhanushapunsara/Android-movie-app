package com.example.movie2.data.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class MovieApiService {
    private val apiKey = "52c7de8"
    private val baseUrl = "https://www.omdbapi.com/"

    suspend fun searchMovieByTitle(title: String): Result<MovieApiResponse> {
        return withContext(Dispatchers.IO) {
            try {
                // Title is already case-insensitive in the API
                val encodedTitle = URLEncoder.encode(title, "UTF-8")
                val url = URL("$baseUrl?t=$encodedTitle&apikey=$apiKey")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                    reader.close()

                    val jsonObject = JSONObject(response.toString())

                    // Check if the response was successful
                    if (jsonObject.optString("Response") == "True") {
                        val movieResponse = MovieApiResponse(
                            Title = jsonObject.optString("Title", ""),
                            Year = jsonObject.optString("Year", ""),
                            Rated = jsonObject.optString("Rated", ""),
                            Released = jsonObject.optString("Released", ""),
                            Runtime = jsonObject.optString("Runtime", ""),
                            Genre = jsonObject.optString("Genre", ""),
                            Director = jsonObject.optString("Director", ""),
                            Writer = jsonObject.optString("Writer", ""),
                            Actors = jsonObject.optString("Actors", ""),
                            Plot = jsonObject.optString("Plot", ""),
                            Response = jsonObject.optString("Response", "")
                        )
                        Result.success(movieResponse)
                    } else {
                        val error = jsonObject.optString("Error", "Unknown error")
                        Result.failure(Exception(error))
                    }
                } else {
                    Result.failure(Exception("HTTP Error: $responseCode"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // New method to search for multiple movies
    suspend fun searchMultipleMovies(searchTerm: String): Result<List<MultipleMovieSearchResult>> {
        return withContext(Dispatchers.IO) {
            try {
                val encodedSearchTerm = URLEncoder.encode(searchTerm, "UTF-8")
                // Use the 's' parameter to search for multiple movies
                val url = URL("$baseUrl?s=$encodedSearchTerm&apikey=$apiKey")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                    reader.close()

                    val jsonObject = JSONObject(response.toString())

                    // Check if the response was successful
                    if (jsonObject.optString("Response") == "True") {
                        val searchResults = mutableListOf<MultipleMovieSearchResult>()
                        val searchArray = jsonObject.getJSONArray("Search")

                        for (i in 0 until searchArray.length()) {
                            val movieObject = searchArray.getJSONObject(i)
                            val movie = MultipleMovieSearchResult(
                                Title = movieObject.optString("Title", ""),
                                Year = movieObject.optString("Year", ""),
                                imdbID = movieObject.optString("imdbID", ""),
                                Type = movieObject.optString("Type", ""),
                                Poster = movieObject.optString("Poster", "")
                            )
                            searchResults.add(movie)
                        }

                        Result.success(searchResults)
                    } else {
                        val error = jsonObject.optString("Error", "Unknown error")
                        Result.failure(Exception(error))
                    }
                } else {
                    Result.failure(Exception("HTTP Error: $responseCode"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
