package nl.yrck.urbandictionary.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import nl.yrck.urbandictionary.R;
import nl.yrck.urbandictionary.api.models.WordInfo;

public class SearchResultsAdapter
        extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {

    private List<WordInfo> data;

    public SearchResultsAdapter(List<WordInfo> data) {
        this.data = data;
    }

    @Override
    public SearchResultsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_result_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WordInfo wordInfo = data.get(position);
        holder.word.setText(wordInfo.word);
        holder.definition.setText(wordInfo.definition);
        holder.example.setText(wordInfo.example);

        holder.thumbsUp.setText(wordInfo.thumbs_up);
        holder.thumbsDown.setText(wordInfo.thumbs_down);

        String id = Integer.toString(wordInfo.defid);
        holder.itemView.setTag(id);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView word;
        TextView definition;
        TextView example;

        TextView thumbsUp;
        TextView thumbsDown;

        ViewHolder(View v) {
            super(v);
            word = (TextView) v.findViewById(R.id.word);
            definition = (TextView) v.findViewById(R.id.definition);
            example = (TextView) v.findViewById(R.id.example);
            thumbsUp = (TextView) v.findViewById(R.id.thumb_up_txt);
            thumbsDown = (TextView) v.findViewById(R.id.thumb_down_txt);
        }
    }
}
