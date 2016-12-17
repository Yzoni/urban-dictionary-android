package nl.yrck.urbandictionary;

import android.content.Intent;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import nl.yrck.urbandictionary.api.models.SearchResult;
import nl.yrck.urbandictionary.firebaseModels.SearchHistoryItem;
import nl.yrck.urbandictionary.firebaseModels.User;
import nl.yrck.urbandictionary.loaders.SearchResultsLoader;

public class MainActivity extends AppCompatActivity
        implements
        SearchResultsFragment.OnFragmentInteractionListener,
        SearchHistoryFragment.OnFragmentInteractionListener {

    public static final String TAG = "MAIN_ACTIVITY";

    private static final int SEARCH_LOADER_ID = 0;

    private String activeFragment = SearchHistoryFragment.TAG;

    private EditText searchField;
    private Button resetButton;
    private SearchResult searchResult;

    private LoaderManager.LoaderCallbacks<SearchResult> searchResultLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchResultLoader = loaderSearchResult();

        searchField = (EditText) findViewById(R.id.search_field);
        searchField.setOnEditorActionListener((view, i, keyEvent) -> onEditTextKey(i));

        resetButton = (Button) findViewById(R.id.search_reset_btn);
        resetButton.setOnClickListener((v) -> onResetButton());

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

        // Handle sign in and out button clicks
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            launchSignIn();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("ACTIVE_FRAGMENT", activeFragment);

        if (searchResult != null) {
            outState.putSerializable("SEARCH_RESULT", searchResult);
        }

        super.onSaveInstanceState(outState);
    }

    /**
     * Swap the bottom fragment with a new fragment displaying the search results
     */
    private void doSearch() {
        String searchTerm = searchField.getText().toString();
        Bundle bundle = new Bundle();
        bundle.putString("SEARCH_TERM", searchTerm);
        if (!activeFragment.equals(SearchResultsFragment.TAG)) {
            setResultFragment(searchResult);
        }
        getSupportLoaderManager()
                .restartLoader(SEARCH_LOADER_ID, bundle, searchResultLoader).forceLoad();

        // Save to history search term to database
        saveSearchHistoryItem(searchTerm);
    }

    /**
     * When history is not yet showing. Swap the bottom fragment with a new fragment displaying
     * the search history. Also clear the search text.
     */
    private void onResetButton() {
        searchField.setText("");
        setHistoryFragment();
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
        switch (item.getItemId()) {
            case R.id.action_clearHistory:
                final String userId = getCurrentUserId();
                DatabaseReference userSearchHistoryReference = FirebaseDatabase.getInstance().getReference()
                        .child("user-searchhistory").child(userId);
                userSearchHistoryReference.removeValue();
            case R.id.action_signOut:
                // Sign out of firebase and launch the login activity
                FirebaseAuth.getInstance().signOut();
                launchSignIn();
                finish();
                return true;
            case R.id.action_about:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Always go back to the search history fragment first, set it another fragment is there
        if (!activeFragment.equals(SearchHistoryFragment.TAG)) {
            setHistoryFragment();
        } else {
            super.onBackPressed();
        }
    }

    private void launchSignIn() {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }

    @Override
    public void setSearchField(String historyTerm) {
        searchField.setText(historyTerm);
        doSearch();
    }

    private void saveSearchHistoryItem(String searchTerm) {
        final String userId = getCurrentUserId();
        DatabaseReference userSearchHistoryReference = FirebaseDatabase.getInstance().getReference()
                .child("user-searchhistory").child(userId);
        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.child("users").child(userId).getValue(User.class);
                        if (user == null) {
                            Toast.makeText(MainActivity.this, "Could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            String key = userSearchHistoryReference.push().getKey();
                            SearchHistoryItem savedItem = new SearchHistoryItem(
                                    searchTerm, System.currentTimeMillis() / 1000L);
                            userSearchHistoryReference.child(key).setValue(savedItem);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "" + databaseError.toException());
                    }
                });
    }

    private SearchHistoryFragment setHistoryFragment() {
        SearchHistoryFragment searchHistoryFragment = SearchHistoryFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_bottom_layout, searchHistoryFragment)
                .commit();

        activeFragment = SearchHistoryFragment.TAG;

        return searchHistoryFragment;
    }

    private SearchResultsFragment setResultFragment(SearchResult searchResult) {
        SearchResultsFragment searchResultsFragment = SearchResultsFragment.newInstance(searchResult);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_bottom_layout, searchResultsFragment)
                .commit();

        activeFragment = SearchResultsFragment.TAG;

        return searchResultsFragment;
    }

    private String getCurrentUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
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
                SearchResultsFragment fragment = (SearchResultsFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.main_bottom_layout);
                searchResult = data;
                fragment.activityDataUpdated(searchResult);
            }

            @Override
            public void onLoaderReset(Loader<SearchResult> loader) {
                Log.e("MAIN", "Search result loader reset");
            }
        };
    }


}
