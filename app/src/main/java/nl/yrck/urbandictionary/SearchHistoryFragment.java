package nl.yrck.urbandictionary;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.List;

import nl.yrck.urbandictionary.adapters.SearchHistoryAdapter;
import nl.yrck.urbandictionary.models.SearchHistoryItem;

public class SearchHistoryFragment extends Fragment {

    public static String TAG = "SEARCH_HIST_FRAGMENT";
    List<SearchHistoryItem> searchHistoryItems;
    private OnFragmentInteractionListener onFragmentInteractionListener;
    private DatabaseReference database;
    private RecyclerView recycler;
    private SearchHistoryAdapter adapter;
    private RecyclerView.LayoutManager lm;

    public SearchHistoryFragment() {
        // Required empty public constructor
    }


    public static SearchHistoryFragment newInstance() {
        SearchHistoryFragment fragment = new SearchHistoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_history, container, false);
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance().getReference().child("user-searchhistory");

        recycler = (RecyclerView) rootView.findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);

        lm = new LinearLayoutManager(getActivity());
        recycler.setLayoutManager(lm);

        Query query = database.child(userId);
        adapter = new SearchHistoryAdapter(SearchHistoryItem.class, R.layout.search_history_item, query);
        adapter.setOnItemClickListener((position, v) -> useHistoryItem(v));
        recycler.setAdapter(adapter);

        return rootView;
    }

    private boolean useHistoryItem(View v) {
        TextView textView = (TextView) v.findViewById(R.id.search_term_txt);
        String searchText = textView.getText().toString();
        onFragmentInteractionListener.setSearchField(searchText);
        return true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            onFragmentInteractionListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onFragmentInteractionListener = null;
    }

    public interface OnFragmentInteractionListener {
        void setSearchField(String term);
    }
}
