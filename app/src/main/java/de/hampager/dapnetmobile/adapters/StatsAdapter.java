package de.hampager.dapnetmobile.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.LinkedHashMap;

import de.hampager.dap4j.models.Stats;
import de.hampager.dapnetmobile.R;

public class StatsAdapter extends RecyclerView.Adapter<StatsAdapter.StatsViewHolder> {
    private LinkedHashMap<String, Integer> map;

    public StatsAdapter(Stats stats) {
        this.map = (LinkedHashMap<String, Integer>) stats.getStats();
    }

    @NonNull
    @Override
    public StatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.stats_item, parent, false);
        return new StatsViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull StatsViewHolder holder, int position) {
        if (position == 7) {
            // "Nodes/Up"
            String label = getNameByIndex(map, position) + "/Up";
            String value = getNumberByIndex(map, position) + "/" + getNumberByIndex(map, position + 1);

            holder.nameTextView.setText(label);
            holder.numberTextView.setText(value);
        }
        else if (position == 8) {
            // "Transmitters/Up"
            // String label = getNameByIndex(map, position + 1) + "/Up"; TODO: find variable to value "Transmitters" and replace with Tx
            String label = "TX/Up";
            String value = getNumberByIndex(map, position + 1)+ "/" + getNumberByIndex(map, position + 2);

            holder.nameTextView.setText(label);
            holder.numberTextView.setText(value);
        }
        else {
            holder.nameTextView.setText(getNameByIndex(map, position));
            holder.numberTextView.setText(String.valueOf(getNumberByIndex(map, position)));
        }

    }

    @Override
    public int getItemCount() {
        return map.size() - 2;
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
            nameTextView = itemView.findViewById(R.id.welcomeStatsName);
            numberTextView = itemView.findViewById(R.id.welcomeStatsNumber);
        }
    }


}
