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
    private HttpLoggingInterceptor logging;

    private boolean enableDebugLogging;

    public UDApi() {
    }

    public UDApi enableDebugLogging(boolean enable) {
        this.enableDebugLogging = enable;
        if (logging != null) {
            logging.setLevel(enable ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        }
        return this;
    }

    protected Retrofit.Builder retrofitBuilder() {
        return new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient());
    }

    protected synchronized OkHttpClient okHttpClient() {
        if (okHttpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            setOkHttpClientDefaults(builder);
            okHttpClient = builder.build();
        }
        return okHttpClient;
    }

    protected void setOkHttpClientDefaults(OkHttpClient.Builder builder) {
//        enableDebugLogging(true);
        if (enableDebugLogging) {
            logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(logging);
        }
    }

    protected Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = retrofitBuilder().build();
        }
        return retrofit;
    }

    public SearchService searchService() {
        return getRetrofit().create(SearchService.class);
    }

}
