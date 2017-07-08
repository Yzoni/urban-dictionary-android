/*
 * Yorick de Boer - 10786015
 */

package nl.yrck.urbandictionary;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import nl.yrck.urbandictionary.adapters.SearchHistoryAdapter;
import nl.yrck.urbandictionary.db.DbHelper;
import nl.yrck.urbandictionary.db.models.SearchHistoryItem;

/*
 * Fragment to display the search history
 */
public class SearchHistoryFragment extends Fragment {

    public static String TAG = "SEARCH_HIST_FRAGMENT";
    SearchHistoryAdapter adapter;
    RecyclerView recycler;
    List<SearchHistoryItem> searchHistoryItems;
    private OnFragmentInteractionListener onFragmentInteractionListener;

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

        recycler = (RecyclerView) rootView.findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);

        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        lm.setStackFromEnd(true);
        lm.setReverseLayout(true);
        recycler.setLayoutManager(lm);

        DbHelper db = new DbHelper(getContext());
        searchHistoryItems = db.listSearchHistoryItems();
        adapter = new SearchHistoryAdapter(searchHistoryItems);

        adapter.setOnItemClickListener((position, v) -> useHistoryItem(v));
        recycler.setAdapter(adapter);

        // Set swipe action to remove history item
        setSwipeOnRecyclerView();

        return rootView;
    }

    /*
     * From a history item view in the list get the term text and put it in the search field.
     */
    private boolean useHistoryItem(View v) {
        TextView textView = (TextView) v.findViewById(R.id.search_term_txt);
        String searchText = textView.getText().toString();
        onFragmentInteractionListener.setSearchField(searchText);
        return true;
    }

    private void setSwipeOnRecyclerView() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        searchHistoryItems.remove(viewHolder.getAdapterPosition());
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }
                };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recycler);
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

        // Set null to avoid memory leak
        onFragmentInteractionListener = null;
    }

    public interface OnFragmentInteractionListener {

        // Set the search field text in activity from this fragment
        void setSearchField(String term);
    }
}
