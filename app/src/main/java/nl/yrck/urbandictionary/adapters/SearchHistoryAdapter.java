package nl.yrck.urbandictionary.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

import nl.yrck.urbandictionary.R;
import nl.yrck.urbandictionary.firebaseModels.SearchHistoryItem;


public class SearchHistoryAdapter extends FirebaseRecyclerAdapter<SearchHistoryItem, SearchHistoryAdapter.ListItemViewHolder> {

    private static SearchHistoryAdapter.ClickListener clickListener;

    public SearchHistoryAdapter(Class<SearchHistoryItem> modelClass, int modelLayout, Query ref) {
        super(modelClass, modelLayout, ListItemViewHolder.class, ref);
    }

    @Override
    protected void populateViewHolder(final SearchHistoryAdapter.ListItemViewHolder viewHolder, final SearchHistoryItem model, final int position) {
        viewHolder.bindToPost(model);
    }

    public void setOnItemClickListener(SearchHistoryAdapter.ClickListener clickListener) {
        SearchHistoryAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    public static class ListItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView term;

        public ListItemViewHolder(View v) {
            super(v);
            term = (TextView) v.findViewById(R.id.search_term_txt);

            v.setOnClickListener(this);
        }

        public void bindToPost(SearchHistoryItem savedItem) {
            term.setText(savedItem.term);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }
    }
}
