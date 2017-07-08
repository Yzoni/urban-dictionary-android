/*
 * Yorick de Boer - 10786015
 */

package nl.yrck.urbandictionary;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import nl.yrck.urbandictionary.api.UDApi;
import nl.yrck.urbandictionary.api.models.SearchResult;
import nl.yrck.urbandictionary.db.DbHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
 * The main activity containing the search field and a layout for dynamic fragment switching
 */
public class MainActivity extends AppCompatActivity
        implements SearchHistoryFragment.OnFragmentInteractionListener {

    public static final String TAG = "MAIN_ACTIVITY";

    private String activeFragment = SearchHistoryFragment.TAG;

    private EditText searchField;
    private ImageButton resetButton;
    private SearchResult searchResult;
    private FrameLayout spinnerLayout;
    private FrameLayout mainBottomLayout;

    private LoaderManager.LoaderCallbacks<SearchResult> searchResultLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchField = (EditText) findViewById(R.id.search_field);
        searchField.setOnEditorActionListener((view, i, keyEvent) -> onEditTextKeyPress(i));

        resetButton = (ImageButton) findViewById(R.id.search_reset_btn);
        resetButton.setOnClickListener((v) -> onResetButton());

        spinnerLayout = (FrameLayout) findViewById(R.id.main_bottom_layout_spinner);
        mainBottomLayout = (FrameLayout) findViewById(R.id.main_bottom_layout);

        if (savedInstanceState != null) {
            searchResult = (SearchResult) savedInstanceState.getSerializable("SEARCH_RESULT");
            activeFragment = savedInstanceState.getString("ACTIVE_FRAGMENT");

            // Set cursor to last character in search field
            searchField.setSelection(searchField.getText().length());
        }

        // Initialize bottom fragment
        if (activeFragment.equals(SearchResultsFragment.TAG)) {
            setResultFragment(searchResult);
        } else {
            setHistoryFragment();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("ACTIVE_FRAGMENT", activeFragment);

        // Save all the search result data
        if (searchResult != null) {
            outState.putSerializable("SEARCH_RESULT", searchResult);
        }

        super.onSaveInstanceState(outState);
    }

    /*
     * Swap the bottom fragment with a new fragment displaying the search results
     */
    private void doSearch() {
        mainBottomLayout.setVisibility(View.GONE);
        spinnerLayout.setVisibility(View.VISIBLE);

        // Push the search query from the edit text search field to the loader and api
        String searchTerm = searchField.getText().toString();
        if (!activeFragment.equals(SearchResultsFragment.TAG)) {
            setResultFragment(searchResult);
        }

        UDApi.getApi().searchService().get(searchTerm).enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                SearchResultsFragment fragment = (SearchResultsFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.main_bottom_layout);
                searchResult = response.body();
                fragment.activityDataUpdated(searchResult);

                // Hide loading spinner and show content
                spinnerLayout.setVisibility(View.GONE);
                mainBottomLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<SearchResult> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Failed getting definitions", Toast.LENGTH_SHORT)
                        .show();
            }
        });

        // Save to history search term to database
        saveSearchHistoryItem(searchTerm);
    }


    /*
     * Reset the search field and set the history fragment if not set
     */
    private void onResetButton() {
        searchField.setText("");
        if (activeFragment.equals(SearchResultsFragment.TAG)) {
            setHistoryFragment();
        }
        searchField.requestFocus();
    }

    private boolean onEditTextKeyPress(int actionId) {
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
        switch (item.getItemId()) {
            case R.id.action_clear_history:
                clearSearchHistory();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Always go back to the search history fragment first, set it when another fragment is
        // there
        if (!activeFragment.equals(SearchHistoryFragment.TAG)) {
            setHistoryFragment();
        } else {
            super.onBackPressed();
        }
    }

    /*
     * Sets the search field from a fragment
     */
    @Override
    public void setSearchField(String historyTerm) {
        searchField.setText(historyTerm);
        searchField.setSelection(searchField.getText().length());
        doSearch();
    }

    /*
     * Save a search term to the firebase database search history, overwriting a previous search
     * with the same search term if exists.
     */
    private void saveSearchHistoryItem(String searchTerm) {
        DbHelper db = new DbHelper(getApplicationContext());

        if (!db.searchHistoryItemExists(searchTerm)) {
            db.createSearchHistoryItem(searchTerm);
        }

        db.close();
    }

    private void clearSearchHistory() {
        DbHelper db = new DbHelper(getApplicationContext());
        db.dropSearchHistory();
        db.close();

        if (activeFragment.equals(SearchHistoryFragment.TAG)) {
            SearchHistoryFragment searchHistoryFragment =
                    (SearchHistoryFragment) getSupportFragmentManager()
                            .findFragmentByTag(SearchHistoryFragment.TAG);
            searchHistoryFragment.searchHistoryItems.clear();
            searchHistoryFragment.adapter.notifyDataSetChanged();
        }
    }

    /*
     * Sets the history fragment as the bottom layout and sets active fragment flag
     */
    private void setHistoryFragment() {
        SearchHistoryFragment searchHistoryFragment = SearchHistoryFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_bottom_layout, searchHistoryFragment, SearchHistoryFragment.TAG)
                .commit();

        activeFragment = SearchHistoryFragment.TAG;
    }

    /*
     * Sets the results fragment as the bottom layout and sets active fragment flag
     */
    private void setResultFragment(SearchResult searchResult) {
        SearchResultsFragment searchResultsFragment = SearchResultsFragment.newInstance(searchResult);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_bottom_layout, searchResultsFragment, SearchResultsFragment.TAG)
                .commit();

        activeFragment = SearchResultsFragment.TAG;
    }
}
