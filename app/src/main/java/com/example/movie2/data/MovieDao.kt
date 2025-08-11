package com.example.movie2.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: Movie)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<Movie>)

    @Query("SELECT * FROM movies WHERE title LIKE '%' || :title || '%'")
    suspend fun searchMoviesByTitle(title: String): List<Movie>

    @Query("SELECT * FROM movies WHERE actors LIKE '%' || :actorName || '%'")
    suspend fun searchMoviesByActor(actorName: String): List<Movie>

    @Query("SELECT COUNT(*) FROM movies")
    suspend fun getMovieCount(): Int

    @Query("DELETE FROM movies")
    suspend fun clearAllMovies()
}
