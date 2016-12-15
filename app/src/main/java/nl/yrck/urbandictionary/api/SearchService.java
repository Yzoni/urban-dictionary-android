package nl.yrck.urbandictionary.api;

import nl.yrck.urbandictionary.api.models.SearchResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface SearchService {
    @GET("term=?{word}")
    Call<SearchResult> get(
            @Path("word") String word
    );
}

