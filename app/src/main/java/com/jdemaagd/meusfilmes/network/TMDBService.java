package com.jdemaagd.meusfilmes.network;

import android.util.Log;

import com.jdemaagd.meusfilmes.common.AppConstants;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TMDBService {

    private static final String LOG_TAG = TMDBService.class.getSimpleName();

    private static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request();
                    HttpUrl httpUrl = request.url();

                    httpUrl = httpUrl.newBuilder()
                            .addQueryParameter("api_key", AppConstants.API_KEY)     // resValue
                            .build();

                    Log.d(LOG_TAG, "TMDBService: HTTP Url:" + httpUrl);

                    request = request.newBuilder().url(httpUrl).build();
                    return chain.proceed(request);
                }
            }).build();

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(AppConstants.BASE_URL)     // resValue??
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = builder.build();

    public static <S> S createService(Class<S> serviceClass) {
        return retrofit.create(serviceClass);
    }
}
