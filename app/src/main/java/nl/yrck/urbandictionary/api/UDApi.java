package nl.yrck.urbandictionary.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UDApi {

    public static final String API_HOST = "api.urbandictionary.com";
    public static final String API_URL = "http://" + API_HOST + "/v0/";

    private OkHttpClient okHttpClient;
    private Retrofit retrofit;

    public UDApi() {
    }

    private Retrofit.Builder retrofitBuilder() {
        return new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient());
    }

    private synchronized OkHttpClient okHttpClient() {
        if (okHttpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            okHttpClient = builder.build();
        }
        return okHttpClient;
    }

    private Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = retrofitBuilder().build();
        }
        return retrofit;
    }

    public SearchService searchService() {
        return getRetrofit().create(SearchService.class);
    }

}
