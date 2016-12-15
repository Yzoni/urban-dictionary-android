package nl.yrck.urbandictionary;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import nl.yrck.urbandictionary.api.models.SearchResult;
import nl.yrck.urbandictionary.loaders.SearchResultsLoader;

public class MainActivity extends AppCompatActivity {

    private static final int SEARCH_LOADER_ID = 0;

    EditText searchField;
    Button searchButton;
    Button resetButton;

    LoaderManager.LoaderCallbacks<SearchResult> searchResultLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchField = (EditText) findViewById(R.id.search_field);

        searchButton = (Button) findViewById(R.id.search_go_btn);
        searchButton.setOnClickListener((v) -> onSearchButton());

        resetButton = (Button) findViewById(R.id.search_reset_btn);
        resetButton.setOnClickListener((v) -> onResetButton());

        searchResultLoader = loaderSearchResult();

        // Initialize with the history bottom fragment
        SearchHistoryFragment searchHistoryFragment = SearchHistoryFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_bottom_layout, searchHistoryFragment)
                .commit();
    }

    /**
     * Swap the bottom fragment with a new fragment displaying the search results
     */
    private void onSearchButton() {
        String searchTerm = searchField.getText().toString();
        Bundle bundle = new Bundle();
        bundle.putString("SEARCH_TERM", searchTerm);
        getSupportLoaderManager().initLoader(SEARCH_LOADER_ID, bundle, searchResultLoader);
    }

    /**
     * When history is not yet showing. Swap the bottom fragment with a new fragment displaying
     * the search history. Also clear the search text.
     */
    private void onResetButton() {
        searchField.setText("");

        SearchHistoryFragment searchHistoryFragment = SearchHistoryFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_bottom_layout, searchHistoryFragment)
                .commit();

        searchField.requestFocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private LoaderManager.LoaderCallbacks<SearchResult> loaderSearchResult() {
        return new LoaderManager.LoaderCallbacks<SearchResult>() {
            @Override
            public Loader<SearchResult> onCreateLoader(int id, Bundle args) {
                String searchTerm = args.getString("SEARCH_TERM");
                return new SearchResultsLoader(getApplication(), searchTerm);
            }

            @Override
            public void onLoadFinished(Loader<SearchResult> loader, SearchResult data) {
                SearchResultsFragment searResultsFragment = SearchResultsFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_bottom_layout, searResultsFragment)
                        .commit();
            }

            @Override
            public void onLoaderReset(Loader<SearchResult> loader) {
                Log.e("MAIN", "Search result loader reset");
            }
        };
    }

}
