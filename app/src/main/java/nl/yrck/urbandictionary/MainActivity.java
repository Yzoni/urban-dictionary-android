package nl.yrck.urbandictionary;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

import nl.yrck.urbandictionary.api.models.SearchResult;
import nl.yrck.urbandictionary.api.models.WordInfo;
import nl.yrck.urbandictionary.loaders.SearchResultsLoader;

public class MainActivity extends AppCompatActivity
        implements
        SearchResultsFragment.OnFragmentInteractionListener,
        SearchHistoryFragment.OnFragmentInteractionListener {

    public static final String TAG = "MAIN_ACTIVITY";

    private static final int SEARCH_LOADER_ID = 0;

    EditText searchField;
    Button resetButton;

    LoaderManager.LoaderCallbacks<SearchResult> searchResultLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchField = (EditText) findViewById(R.id.search_field);
        searchField.setOnEditorActionListener((view, i, keyEvent) -> onEditTextKey(i));

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
    private void doSearch() {

        String searchTerm = searchField.getText().toString();
        Bundle bundle = new Bundle();
        bundle.putString("SEARCH_TERM", searchTerm);
        SearchResultsFragment searResultsFragment = SearchResultsFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_bottom_layout, searResultsFragment)
                .commit();
        getSupportLoaderManager()
                .restartLoader(SEARCH_LOADER_ID, bundle, searchResultLoader).forceLoad();

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

    private boolean onEditTextKey(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            doSearch();
        }
        return true;
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
                Log.d(TAG, "test button search");
                String searchTerm = args.getString("SEARCH_TERM");
                Log.d(TAG, searchTerm);
                return new SearchResultsLoader(getApplication(), searchTerm);
            }

            @Override
            public void onLoadFinished(Loader<SearchResult> loader, SearchResult data) {
                Log.d(TAG, "loader finished");
                List<WordInfo> wordInfoList = data.wordInfos;
                for (WordInfo wordInfo : wordInfoList) {
                    System.out.println(wordInfo.definition);
                }
                SearchResultsFragment fragment = (SearchResultsFragment) getSupportFragmentManager().findFragmentById(R.id.main_bottom_layout);
                fragment.activityDataUpdated(data);
            }

            @Override
            public void onLoaderReset(Loader<SearchResult> loader) {
                Log.e("MAIN", "Search result loader reset");
            }
        };
    }


}
