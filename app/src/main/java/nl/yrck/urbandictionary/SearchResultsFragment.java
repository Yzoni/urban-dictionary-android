package nl.yrck.urbandictionary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import nl.yrck.urbandictionary.adapters.SearchResultsAdapter;
import nl.yrck.urbandictionary.api.models.SearchResult;
import nl.yrck.urbandictionary.api.models.WordInfo;


public class SearchResultsFragment extends Fragment {

    public static final String TAG = "SEARCHRESULT_FRAG";
    SearchResult searchResult;
    List<WordInfo> wordInfos = new ArrayList<>();
    private RecyclerView recycler;
    private SearchResultsAdapter adapter;
    private RecyclerView.LayoutManager lm;

    public SearchResultsFragment() {
        // Required empty public constructor
    }

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
            searchResult = (SearchResult) args.getSerializable("SEARCH_RESULT");
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

        recycler = (RecyclerView) rootView.findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);

        lm = new LinearLayoutManager(getActivity());
        recycler.setLayoutManager(lm);

        adapter = new SearchResultsAdapter(wordInfos);
        adapter.notifyDataSetChanged();
        adapter.setOnLinkButtonClickListener((position, view) -> onLinkButton(position));

        recycler.setAdapter(adapter);

        return rootView;
    }

    public void onLinkButton(int position) {
        WordInfo wordInfo = wordInfos.get(position);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                wordInfo.word + "\r\n\n" + wordInfo.definition + "\r\n\n" + wordInfo.permalink);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    public void activityDataUpdated(SearchResult searchResult) {
        wordInfos.clear();
        wordInfos.addAll(searchResult.wordInfos);
        adapter.notifyDataSetChanged();
    }
}
