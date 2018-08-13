package de.hampager.dapnetmobile.filters;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

import de.hampager.dap4j.models.Transmitter;
import de.hampager.dapnetmobile.adapters.TransmitterAdapter;

public class TransmitterFilter extends Filter {
    private TransmitterAdapter adapter;
    private List<Transmitter> filterList;

    public TransmitterFilter(List<Transmitter> filterList, TransmitterAdapter adapter) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    //FILTERING OCCURS
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        //CHECK CONSTRAINT VALIDITY
        if (constraint != null && constraint.length() > 0) {
            //CHANGE TO UPPER
            constraint = constraint.toString().toUpperCase();
            //STORE OUR FILTERED PLAYERS
            List<Transmitter> filteredTransmitters = new ArrayList<>();
            for (Transmitter hamnetCall : filterList) {
                //CHECK
/*
                String text = hamnetCall.getText().toUpperCase();
                List<String> TransmitterNames = hamnetCall.getTransmitterNames();
                List<String> transmitterGroupNames = hamnetCall.getTransmitterGroupNames();
                String ownerName = hamnetCall.getOwnerName();
                for(int i=0;i<TransmitterNames.size();i++){
                    TransmitterNames.set(i,TransmitterNames.get(i).toUpperCase());
                }
                for(int i=0;i<transmitterGroupNames.size();i++){
                    transmitterGroupNames.set(i,transmitterGroupNames.get(i).toUpperCase());
                }
                if(text.contains(constraint)||ownerName.contains(constraint)||TransmitterNames.contains(constraint.toString()))
                {
                    //ADD CALL TO FILTERED
                    filteredTransmitters.add(hamnetCall);
                }*/
            }
            results.count = filteredTransmitters.size();
            results.values = filteredTransmitters;
        } else {
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.setmValues((ArrayList<Transmitter>) results.values);
        //REFRESH
        adapter.notifyDataSetChanged();
    }
}
