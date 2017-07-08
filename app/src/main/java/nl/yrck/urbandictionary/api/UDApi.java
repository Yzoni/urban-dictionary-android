/*
 * Yorick de Boer - 10786015
 */

package nl.yrck.urbandictionary.api;

import android.support.annotation.NonNull;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*
 * Main API class for the Urban Dictionary API, exposes the search service
 */
public class UDApi {

    private static final String API_HOST = "api.urbandictionary.com";
    private static final String API_URL = "http://" + API_HOST + "/v0/";
    private static UDApi udApi;
    private OkHttpClient okHttpClient;
    private Retrofit retrofit;

    public static UDApi getApi() {
        if (udApi == null) {
            udApi = new UDApi();
        }
        return udApi;
    }

    @NonNull
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

    /*
     * Gets the search service
     */
    public SearchService searchService() {
        return getRetrofit().create(SearchService.class);
    }

}
