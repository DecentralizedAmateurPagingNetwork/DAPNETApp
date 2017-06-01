package de.hampager.dapnetmobile.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.LinkedHashMap;

import de.hampager.dapnetmobile.R;
import de.hampager.dapnetmobile.api.StatsResource;

public class StatsAdapter extends RecyclerView.Adapter<StatsAdapter.StatsViewHolder> {
    private LinkedHashMap<String, Integer> map = new LinkedHashMap<>();

    public StatsAdapter(StatsResource stats) {
        this.map = stats.getStats();
    }

    @Override
    public StatsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.stats_item, parent, false);
        return new StatsViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(StatsViewHolder holder, int position) {
        holder.nameTextView.setText(getNameByIndex(map, position));
        holder.numberTextView.setText(String.valueOf(getNumberByIndex(map, position)));
    }


    @Override
    public int getItemCount() {
        return map.size();
    }

    private String getNameByIndex(LinkedHashMap<String, Integer> hMap, int index) {
        return (String) hMap.keySet().toArray()[index];

    }

    private Integer getNumberByIndex(LinkedHashMap<String, Integer> hMap, int index) {
        return (Integer) hMap.values().toArray()[index];
    }

    public static class StatsViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView numberTextView;

        public StatsViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.welcomeStatsName);
            numberTextView = (TextView) itemView.findViewById(R.id.welcomeStatsNumber);
        }
    }


}
