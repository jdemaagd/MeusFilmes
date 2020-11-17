package com.jdemaagd.meusfilmes.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.palette.graphics.Palette;

import com.jdemaagd.meusfilmes.R;
import com.jdemaagd.meusfilmes.common.AppConstants;
import com.jdemaagd.meusfilmes.common.AppDatabase;
import com.jdemaagd.meusfilmes.databinding.ActivityDetailsMovieBinding;
import com.jdemaagd.meusfilmes.models.Movie;
import com.jdemaagd.meusfilmes.common.AppExecutor;
import com.jdemaagd.meusfilmes.network.TMDBClient;
import com.jdemaagd.meusfilmes.network.TMDBService;
import com.jdemaagd.meusfilmes.viewmodels.MovieDetailsViewModel;
import com.jdemaagd.meusfilmes.viewmodels.ViewModelFactory;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class MovieDetailsActivity extends AppCompatActivity {

    private static final String LOG_TAG = MovieDetailsActivity.class.getSimpleName();

    private TMDBClient mApiClient;
    private AppDatabase mAppDatabase;
    private ActivityDetailsMovieBinding mBinding;
    private int mColor;
    private boolean mIsFav;
    private Movie mMovie;
    private Target mTargetBackdrop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_details_movie);
        // mBinding.setLifecycleOwner(this);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int width = displaymetrics.widthPixels;
            mBinding.collapsingToolbar.getLayoutParams().height = (int) Math.round(width / 1.5);
        }

        Intent intent = getIntent();

        if (intent != null) {
            mApiClient = TMDBService.createService(TMDBClient.class);
            mAppDatabase = AppDatabase.getInstance(getApplicationContext());
            mMovie = intent.getParcelableExtra(getString(R.string.extra_movie));

            mBinding.setMovie(mMovie);
            mBinding.setPresenter(this);

//            setSupportActionBar(mBinding.toolbar);
//
//            if (getSupportActionBar() != null) {
//                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            }

            mIsFav = false;

            loadMovie();
        }
    }

    private void bindFavIcon() {
        mBinding.movieDetails.ivFavorite.setOnClickListener((view) -> {
            AppExecutor.getInstance().diskIO().execute(() -> {
                if (mIsFav) {
                    Log.d(LOG_TAG, "Remove movie from database via Room.");
                    mAppDatabase.movieDao().removeMovie(mMovie);
                } else {
                    Log.d(LOG_TAG, "Insert movie into database via Room.");
                    mAppDatabase.movieDao().addMovie(mMovie);
                }
                runOnUiThread(() -> setFavIcon());
            });
        });
    }

    private void loadMovie() {
        if (mMovie != null) {
            mTargetBackdrop = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    mBinding.backdrop.setImageBitmap(bitmap);
                    Palette.from(bitmap).generate(palette -> {
                        mColor = palette.getMutedColor(R.attr.colorPrimary) | 0xFF000000;
                        mBinding.collapsingToolbar.setContentScrimColor(mColor);
                        mBinding.collapsingToolbar.setStatusBarScrimColor(mColor);
                    });
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            };

            Picasso.get()
                    .load("http://image.tmdb.org/t/p/w780" + mMovie.getBackdropPath())
                    .into(mTargetBackdrop);

            mBinding.movieDetails.tvErrorMessage.setText(mMovie.getSynopsis());
            mBinding.movieDetails.tvOriginalTitle.setText(mMovie.getOriginalTitle());
            mBinding.movieDetails.tvReleaseDate.setText(mMovie.getReleaseDate());
            mBinding.movieDetails.tvVoteAverage.setText(String.valueOf(mMovie.getUserRating()));
            mBinding.movieDetails.tvOverview.setText(mMovie.getSynopsis());

            Picasso.get()
                    .load(AppConstants.POSTER_URL + mMovie.getPosterPath())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error)
                    .into(mBinding.movieDetails.ivPosterThumb);

            bindFavIcon();
            setFavIcon();
        } else {
            Toast.makeText(MovieDetailsActivity.this, getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            showErrorMessage();
        }

    }

    private void setFavIcon() {
        ViewModelFactory factory = new ViewModelFactory(mAppDatabase, mMovie.getMovieId());
        final MovieDetailsViewModel movieDetailsViewModel = new ViewModelProvider(this, factory).get(MovieDetailsViewModel.class);
        movieDetailsViewModel.getMovie().observe(this, favMovie -> {
            // movieDetailsViewModel.getMovie().removeObserver(this);
            Log.d(LOG_TAG, "Receiving LiveData Update.");
            if (favMovie == null) {
                mIsFav = false;
                mBinding.movieDetails.ivFavorite.setImageResource(R.drawable.ic_heart_border_pink_24dp);
            } else {
                mIsFav = true;
                mBinding.movieDetails.ivFavorite.setImageResource(R.drawable.ic_heart_pink_24dp);
            }
        });
    }

    private void showErrorMessage() {
        mBinding.movieDetails.ivFavorite.setVisibility(View.INVISIBLE);
        mBinding.movieDetails.ivPosterThumb.setVisibility(View.INVISIBLE);
        mBinding.movieDetails.tvOriginalTitle.setVisibility(View.INVISIBLE);
        mBinding.movieDetails.tvOverview.setVisibility(View.INVISIBLE);
        mBinding.movieDetails.tvReleaseDate.setVisibility(View.INVISIBLE);
        mBinding.movieDetails.tvVoteAverage.setVisibility(View.INVISIBLE);

        mBinding.movieDetails.tvErrorMessage.setVisibility(View.VISIBLE);
    }
}
