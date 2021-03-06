package com.jdemaagd.meusfilmes.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.jdemaagd.meusfilmes.models.Movie;

import java.util.List;

@Dao
public interface MovieDao {

    @Insert
    void addMovie(Movie movie);

    @Query("SELECT * FROM Movie WHERE id = :movieId")
    LiveData<Movie> getMovieById(int movieId);

    @Query("SELECT * FROM Movie")
    LiveData<List<Movie>> getAllMovies();

    @Delete
    void removeMovie(Movie movie);

    @Query("DELETE FROM Movie")
    void resetTable();
}
