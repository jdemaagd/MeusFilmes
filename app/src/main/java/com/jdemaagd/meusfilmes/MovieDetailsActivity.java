package com.jdemaagd.meusfilmes;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.app.LoaderManager.LoaderCallbacks;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import com.jdemaagd.meusfilmes.models.Movie;
import com.jdemaagd.meusfilmes.network.JsonUtils;
import com.jdemaagd.meusfilmes.network.UrlUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

public class MovieDetailsActivity extends AppCompatActivity implements LoaderCallbacks<Movie> {

    private static final String LOG_TAG = MovieDetailsActivity.class.getSimpleName();

    private LoaderCallbacks<Movie> mCallback;
    private Movie mMovie;
    private int mMovieId;

    private static final int MOVIE_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_movie);

        Intent intent = getIntent();

        if (intent != null) {
            Bundle movieBundle = intent.getExtras();
            // mMovie = (Movie) movieBundle.getSerializable("MOVIE");
            mMovieId = intent.getIntExtra("MOVIE_ID", 0);

            // TODO: reviews and trailers to view
            // ../id/reviews
            // ../id/videos
        }

        int loaderId = MOVIE_LOADER_ID;
        mCallback = MovieDetailsActivity.this;
        LoaderManager.getInstance(this).initLoader(loaderId, null, mCallback);
    }

    /**
     * Instantiate and return a new Loader for the given ID
     * @param id The ID whose loader is to be created
     * @param loaderArgs Any arguments supplied by the caller
     * @return Return a new Loader instance that is ready to start loading
     */
    @Override
    public Loader<Movie> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<Movie>(this) {
            Movie mMovie = null;

            @Override
            protected void onStartLoading() {
                if (mMovie != null) {
                    deliverResult(mMovie);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Movie loadInBackground() {

                try {
                    URL movieRequestUrl = UrlUtils.buildMovieUrl(mMovieId);
                    return JsonUtils.getMovieFromJson(UrlUtils.getResponseFromRequestUrl(movieRequestUrl));
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(Movie movie) {
                mMovie = movie;
                super.deliverResult(movie);
            }
        };
    }

    /**
     * Called when a previously created loader has finished its load
     * @param loader The Loader that has finished
     * @param movie The data generated by the Loader
     */
    @Override
    public void onLoadFinished(Loader<Movie> loader, Movie movie) {
        if (null == movie) {
            // showErrorMessage();
        } else {
            mMovie = movie;
            bindViews();
        }
    }

    /**
     * Called when a previously created loader is being reset, and thus making its data unavailable
     * The application should at this point remove any references it has to the Loader's data
     * @param loader The Loader that is being reset
     */
    @Override
    public void onLoaderReset(Loader<Movie> loader) {

    }

    /**
     * This method is used when we are resetting data,
     *   so that at one point in time during a refresh of our data,
     *   you can see that there is no data showing
     */
    private void invalidateData() {

    }

    private void bindViews() {
        TextView originalTitleTextView = findViewById(R.id.tv_original_title);
        originalTitleTextView.setText(mMovie.getOriginalTitle());
        TextView releaseYearTextView = findViewById(R.id.tv_release_year);
        releaseYearTextView.setText(mMovie.getReleaseYear());
        TextView durationTextView = findViewById(R.id.tv_duration);
        durationTextView.setText(mMovie.getDuration() + " mins");
        TextView voteAvgTextView = findViewById(R.id.tv_vote_average);
        voteAvgTextView.setText(mMovie.getVoteAverage());
        TextView overViewTextView = findViewById(R.id.tv_overview);
        overViewTextView.setText(mMovie.getOverview());

        ImageView posterImageView = findViewById(R.id.iv_poster_thumb);
        Picasso.get()
                .load(mMovie.getPosterUrl())
                .into(posterImageView);

        ImageView favoriteIcon = findViewById(R.id.iv_favorite);
        favoriteIcon.setImageResource(R.drawable.ic_star_border_yellow_24px);
    }
}
