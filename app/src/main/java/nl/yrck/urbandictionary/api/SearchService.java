package nl.yrck.urbandictionary.api;

import nl.yrck.urbandictionary.api.models.SearchResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SearchService {
    @GET("define")
    Call<SearchResult> get(
            @Query("term") String word
    );
}

