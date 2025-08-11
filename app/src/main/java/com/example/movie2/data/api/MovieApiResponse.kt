package com.example.movie2.data.api

import com.example.movie2.data.Movie

data class MovieApiResponse(
    val Title: String = "",
    val Year: String = "",
    val Rated: String = "",
    val Released: String = "",
    val Runtime: String = "",
    val Genre: String = "",
    val Director: String = "",
    val Writer: String = "",
    val Actors: String = "",
    val Plot: String = "",
    val Response: String = "",
    val Error: String? = null
) {
    // This function is no longer used directly, but kept for reference
    fun toMovie(): Movie {
        return Movie(
            title = Title,
            year = Year,
            rated = Rated,
            released = Released,
            runtime = Runtime,
            genre = Genre,
            director = Director,
            writer = Writer,
            actors = Actors,
            plot = Plot
        )
    }
}
