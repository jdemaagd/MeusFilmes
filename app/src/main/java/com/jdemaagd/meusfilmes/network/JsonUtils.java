package com.jdemaagd.meusfilmes.network;

import com.jdemaagd.meusfilmes.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// Note: final modifier since class does not extend any other classes
public final class JsonUtils {

    private static final String MOVIE_ID = "id";
    private static final String BACKDROP_PATH = "backdrop_path";
    private static final String POPULARITY = "popularity";
    private static final String POSTER_PATH  = "poster_path";
    private static final String USER_RATING = "vote_average";

    public static List<Movie> getMoviesFromJson(String moviesJson) throws JSONException {

        final String MOVIES = "results";

        JSONObject movies = new JSONObject(moviesJson);
        JSONArray moviesArray = movies.getJSONArray(MOVIES);

        List<Movie> parsedMovies = new ArrayList<>();

        for (int i = 0; i < moviesArray.length(); i++) {
            JSONObject jsonMovie = moviesArray.getJSONObject(i);

            int movieId = jsonMovie.getInt(MOVIE_ID);
            double popularity = jsonMovie.getDouble(POPULARITY);
            String posterPath = jsonMovie.getString(POSTER_PATH);
            String backdropPath = jsonMovie.getString(BACKDROP_PATH);
            double userRating = jsonMovie.getDouble(USER_RATING);

            Movie movie = new Movie(movieId, backdropPath, popularity, posterPath, userRating);

            parsedMovies.add(movie);
        }

        return parsedMovies;
    }

    public static Movie getMovieFromJson(String movieJson) throws JSONException {

        final String DURATION = "runtime";
        final String ORIGINAL_TITLE = "original_title";
        final String RELEASE_DATE = "release_date";
        final String SYNOPSIS = "overview";
        final String VOTE_COUNT = "vote_count";

        JSONObject movieObj = new JSONObject(movieJson);
        int movieId = movieObj.getInt(MOVIE_ID);
        String backdropPath = movieObj.getString(BACKDROP_PATH);
        int duration = movieObj.getInt(DURATION);
        String originalTitle = movieObj.getString(ORIGINAL_TITLE);
        double popularity = movieObj.getDouble(POPULARITY);
        String posterPath = movieObj.getString(POSTER_PATH);
        String releaseDate = movieObj.getString(RELEASE_DATE);
        String synopsis = movieObj.getString(SYNOPSIS);
        double userRating = movieObj.getDouble(USER_RATING);
        int voteCount = movieObj.getInt(VOTE_COUNT);

        return new Movie(movieId, backdropPath, duration, originalTitle,
                popularity, posterPath, releaseDate, synopsis, userRating, voteCount);
    }
}
