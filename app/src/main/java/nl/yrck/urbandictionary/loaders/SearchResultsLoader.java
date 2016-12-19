package nl.yrck.urbandictionary.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.io.IOException;

import nl.yrck.urbandictionary.api.UDApi;
import nl.yrck.urbandictionary.api.models.SearchResult;
import retrofit2.Call;

/*
 * Loader for the search result from the UB API
 */
public class SearchResultsLoader extends AsyncTaskLoader<SearchResult> {

    public static String TAG = "SEARCH_LOADER";

    private String searchTerm;

    public SearchResultsLoader(Context context, String searchTerm) {
        super(context);
        this.searchTerm = searchTerm;
    }

    @Override
    protected void onStartLoading() {
        if (takeContentChanged())
            forceLoad();
    }

    @Override
    public SearchResult loadInBackground() {
        Log.d(TAG, "Load in background search started");
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
