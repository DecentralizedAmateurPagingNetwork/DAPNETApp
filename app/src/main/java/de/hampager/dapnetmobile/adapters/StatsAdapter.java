package de.hampager.dapnetmobile.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import de.hampager.dapnetmobile.R;
import de.hampager.dapnetmobile.api.StatsResource;

public class StatsAdapter extends RecyclerView.Adapter<StatsAdapter.StatsViewHolder>{
    LinkedHashMap<String,Integer> map = new LinkedHashMap<>();

    public StatsAdapter (StatsResource stats){
        Log.i("StatsAdap","StatsAdapter");
        Log.i("StatsAdap","Users: "+stats.getStats().get("Users"));
        Log.i("StatsAdap","Users: "+getNumberByIndex(stats.getStats(),0));

        this.map=stats.getStats();
    }
    @Override
    public StatsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.stats_item, parent, false);
        return new StatsViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(StatsViewHolder holder, int position) {
        Log.i("StatsAdap","inside onbindviewholder");
        holder.nameTextView.setText(getNameByIndex(map,position));
        holder.numberTextView.setText(""+getNumberByIndex(map,position));
    }


    @Override
    public int getItemCount() {
        return map.size();
    }

    public static class StatsViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView numberTextView;
        public StatsViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView)itemView.findViewById(R.id.welcomeStatsName);
            numberTextView = (TextView)itemView.findViewById(R.id.welcomeStatsNumber);
        }
    }


    public String getNameByIndex(LinkedHashMap<String, Integer> hMap, int index){
        return (String) hMap.keySet().toArray()[index];

    }


    public Integer getNumberByIndex(LinkedHashMap<String, Integer> hMap, int index){
        return (Integer) hMap.values().toArray()[index];
    }


}
