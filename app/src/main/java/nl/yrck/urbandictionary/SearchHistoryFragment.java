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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import nl.yrck.urbandictionary.adapters.SearchHistoryAdapter;
import nl.yrck.urbandictionary.firebaseModels.SearchHistoryItem;

/*
 * Fragment to display the search history
 */
public class SearchHistoryFragment extends Fragment {

    public static String TAG = "SEARCH_HIST_FRAGMENT";
    private OnFragmentInteractionListener onFragmentInteractionListener;

    SearchHistoryAdapter adapter;
    RecyclerView recycler;

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

        // Setup the database reference
        DatabaseReference database = FirebaseDatabase.getInstance().getReference()
                .child("user-searchhistory");

        recycler = (RecyclerView) rootView.findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);

        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        lm.setStackFromEnd(true);
        lm.setReverseLayout(true);
        recycler.setLayoutManager(lm);

        // Setup adapter with search history data
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query query = database.child(userId);
        adapter = new SearchHistoryAdapter(SearchHistoryItem.class,
                R.layout.search_history_item, query.orderByChild("timestamp"));

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
                        adapter.getRef(viewHolder.getAdapterPosition()).removeValue();
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
