/*
 * Yorick de Boer - 10786015
 */

package nl.yrck.urbandictionary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import nl.yrck.urbandictionary.adapters.SearchResultsAdapter;
import nl.yrck.urbandictionary.api.models.SearchResult;
import nl.yrck.urbandictionary.api.models.WordInfo;

/*
 * This fragment displays the search results in a recyclerview
 */
public class SearchResultsFragment extends Fragment {

    public static final String TAG = "SEARCHRESULT_FRAG";
    private List<WordInfo> wordInfos = new ArrayList<>();
    private SearchResultsAdapter adapter;
    private TextView noResults;

    public SearchResultsFragment() {
        // Required empty public constructor
    }

    /*
     * Create a new instance of the fragment giving a SearchResult object as argument
     */
    public static SearchResultsFragment newInstance(SearchResult searchResult) {
        SearchResultsFragment fragment = new SearchResultsFragment();
        Bundle args = new Bundle();
        args.putSerializable("SEARCH_RESULT", searchResult);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle args = getArguments();
            SearchResult searchResult = (SearchResult) args.getSerializable("SEARCH_RESULT");
            // Search result could be null when the loader is not finished getting the result data
            if (searchResult != null) {
                wordInfos.clear();
                wordInfos.addAll(searchResult.wordInfos);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search_results, container, false);

        noResults = (TextView) rootView.findViewById(R.id.no_results_txt);
        if (wordInfos.size() <= 0) {
            noResults.setVisibility(View.VISIBLE);
        }

        RecyclerView recycler = (RecyclerView) rootView.findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);

        RecyclerView.LayoutManager lm = new LinearLayoutManager(getActivity());
        recycler.setLayoutManager(lm);

        adapter = new SearchResultsAdapter(wordInfos);
        adapter.notifyDataSetChanged();
        adapter.setOnLinkButtonClickListener((position, view) -> onLinkButton(position));

        recycler.setAdapter(adapter);

        return rootView;
    }

    /*
     * Launches a text share intent containing a word definition and permalink
     * from a result element.
     */
    private void onLinkButton(int position) {
        WordInfo wordInfo = wordInfos.get(position);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                wordInfo.word + "\r\n\n" + wordInfo.definition + "\r\n\n" + wordInfo.permalink);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    /*
     * Used to refresh the fragment data from an activity
     */
    public void activityDataUpdated(SearchResult searchResult) {
        wordInfos.clear();
        if (searchResult != null) {
            wordInfos.addAll(searchResult.wordInfos);
        }

        // Make the text indicating no results could be found visible
        if (wordInfos.size() <= 0) {
            noResults.setVisibility(View.VISIBLE);
        } else {
            noResults.setVisibility(View.GONE);
        }

        adapter.notifyDataSetChanged();
    }
}
