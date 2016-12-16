package nl.yrck.urbandictionary.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import nl.yrck.urbandictionary.R;
import nl.yrck.urbandictionary.api.models.WordInfo;

public class SearchResultsAdapter
        extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {

    private static ClickListener clickListener;
    private List<WordInfo> data;
    private Context context;

    public SearchResultsAdapter(List<WordInfo> data, Context context) {
        this.data = data;
        this.context = context;
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        SearchResultsAdapter.clickListener = clickListener;
    }

    @Override
    public SearchResultsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_result_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.definition.setText(data.get(position).definition);
//        holder.example.setText(data.get(position).example);
        String id = Integer.toString(data.get(position).defid);
        holder.itemView.setTag(id);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        public TextView definition;
//        public TextView example;


        public ViewHolder(View v) {
            super(v);
            definition = (TextView) v.findViewById(R.id.definition);
//            example = (TextView) v.findViewById(R.id.example);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }
    }
}
