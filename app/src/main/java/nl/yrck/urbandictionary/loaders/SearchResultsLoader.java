package nl.yrck.urbandictionary.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.io.IOException;

import nl.yrck.urbandictionary.api.UDApi;
import nl.yrck.urbandictionary.api.models.SearchResult;
import retrofit2.Call;

public class SearchResultsLoader extends AsyncTaskLoader<SearchResult> {

    public static String TAG = "SEARCH_LOADER";

    String searchTerm;

    public SearchResultsLoader(Context context, String searchTerm) {
        super(context);
        this.searchTerm = searchTerm;
    }

    @Override
    public SearchResult loadInBackground() {
        try {
            Call<SearchResult> searchResultCall = new UDApi().searchService().get(
                    this.searchTerm
            );
            return searchResultCall.execute().body();
        } catch (IOException e) {
            Log.e(TAG, "Search failed");
        }
        return null;
    }
}
