/*
 * Yorick de Boer - 10786015
 */

package nl.yrck.urbandictionary.api;

import nl.yrck.urbandictionary.api.models.SearchResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/*
 * Used to execute a search on the API
 */
public interface SearchService {
    @GET("define")
    Call<SearchResult> get(
            @Query("term") String word
    );
}

