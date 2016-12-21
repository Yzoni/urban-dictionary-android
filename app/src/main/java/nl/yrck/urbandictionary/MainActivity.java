/*
 * Yorick de Boer - 10786015
 */

package nl.yrck.urbandictionary;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import nl.yrck.urbandictionary.api.models.SearchResult;
import nl.yrck.urbandictionary.dialogs.AboutDialog;
import nl.yrck.urbandictionary.firebaseModels.SearchHistoryItem;
import nl.yrck.urbandictionary.firebaseModels.User;
import nl.yrck.urbandictionary.loaders.SearchResultsLoader;

/*
 * The main activity containing the search field and a layout for dynamic fragment switching
 */
public class MainActivity extends AppCompatActivity
        implements SearchHistoryFragment.OnFragmentInteractionListener {

    public static final String TAG = "MAIN_ACTIVITY";

    private static final int SEARCH_LOADER_ID = 0;

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

        // Test if user is singed in, if not then force launch sign in activity
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            launchSignIn();
            return;
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchResultLoader = loaderSearchResult();

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
            case R.id.action_signOut:
                // Sign out of firebase and launch the login activity
                FirebaseAuth.getInstance().signOut();
                launchSignIn();
                finish();
                return true;
            case R.id.action_about:
                AboutDialog.show(this);
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

    private void launchSignIn() {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }

    /*
     * Sets the search field from a fragment
     */
    @Override
    public void setSearchField(String historyTerm) {
        searchField.setText(historyTerm);
    }

    /*
     * Save a search term to the firebase database search history, overwriting a previous search
     * with the same search term if exists.
     */
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
                            userSearchHistoryReference.push();
                            SearchHistoryItem savedItem = new SearchHistoryItem(
                                    searchTerm, System.currentTimeMillis() / 1000L);
                            userSearchHistoryReference.child(Integer.toString(searchTerm.hashCode()))
                                    .setValue(savedItem);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "" + databaseError.toException());
                    }
                });
    }

    /*
     * Sets the history fragment as the bottom layout and sets active fragment flag
     */
    private void setHistoryFragment() {
        SearchHistoryFragment searchHistoryFragment = SearchHistoryFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_bottom_layout, searchHistoryFragment)
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
                .replace(R.id.main_bottom_layout, searchResultsFragment)
                .commit();

        activeFragment = SearchResultsFragment.TAG;
    }

    /*
     * Gets the currently logged in user id
     */
    @NonNull
    private String getCurrentUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    /*
     * Loader implementation for the the search results
     */
    private LoaderManager.LoaderCallbacks<SearchResult> loaderSearchResult() {
        return new LoaderManager.LoaderCallbacks<SearchResult>() {
            @Override
            public Loader<SearchResult> onCreateLoader(int id, Bundle args) {
                String searchTerm = args.getString("SEARCH_TERM");
                return new SearchResultsLoader(getApplication(), searchTerm);
            }

            @Override
            public void onLoadFinished(Loader<SearchResult> loader, SearchResult data) {
                // Update list data inside the fragment
                SearchResultsFragment fragment = (SearchResultsFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.main_bottom_layout);
                searchResult = data;
                fragment.activityDataUpdated(searchResult);

                // Hide loading spinner and show content
                spinnerLayout.setVisibility(View.GONE);
                mainBottomLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoaderReset(Loader<SearchResult> loader) {
                Log.e("MAIN", "Search result loader reset");
            }
        };
    }


}
