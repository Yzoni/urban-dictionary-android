/*
 * Yorick de Boer - 10786015
 */

package nl.yrck.urbandictionary.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import nl.yrck.urbandictionary.R;
import nl.yrck.urbandictionary.db.models.SearchHistoryItem;


public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder> {

    private static SearchHistoryAdapter.ClickListener clickListener;

    private List<SearchHistoryItem> searchHistoryItems;

    public SearchHistoryAdapter(List<SearchHistoryItem> searchHistoryItems) {
        this.searchHistoryItems = searchHistoryItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View tonciView = inflater.inflate(R.layout.search_history_item, parent, false);
        return new ViewHolder(tonciView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.term.setText(searchHistoryItems.get(position).getTerm());
    }

    @Override
    public int getItemCount() {
        return searchHistoryItems.size();
    }


    public void setOnItemClickListener(SearchHistoryAdapter.ClickListener clickListener) {
        SearchHistoryAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    // Requires public
    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public TextView term;

        public ViewHolder(View v) {
            super(v);
            term = (TextView) v.findViewById(R.id.search_term_txt);

            // Set click listener on the whole view
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }
    }
}
