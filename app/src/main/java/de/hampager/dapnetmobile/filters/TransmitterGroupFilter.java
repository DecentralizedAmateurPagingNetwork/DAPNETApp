package de.hampager.dapnetmobile.filters;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

import de.hampager.dap4j.models.TransmitterGroup;
import de.hampager.dapnetmobile.adapters.TransmitterGroupAdapter;

public class TransmitterGroupFilter extends Filter {
    private TransmitterGroupAdapter adapter;
    private List<TransmitterGroup> filterList;

    public TransmitterGroupFilter(List<TransmitterGroup> filterList, TransmitterGroupAdapter adapter) {
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
            List<TransmitterGroup> filteredTransmitterGroups = new ArrayList<>();
            for (TransmitterGroup hamnetCall : filterList) {
                //CHECK
/*
                String text = hamnetCall.getText().toUpperCase();
                List<String> TransmitterGroupNames = hamnetCall.getTransmitterGroupNames();
                List<String> TransmitterGroupNames = hamnetCall.getTransmitterGroupNames();
                String ownerName = hamnetCall.getOwnerName();
                for(int i=0;i<TransmitterGroupNames.size();i++){
                    TransmitterGroupNames.set(i,TransmitterGroupNames.get(i).toUpperCase());
                }
                for(int i=0;i<TransmitterGroupNames.size();i++){
                    TransmitterGroupNames.set(i,TransmitterGroupNames.get(i).toUpperCase());
                }
                if(text.contains(constraint)||ownerName.contains(constraint)||TransmitterGroupNames.contains(constraint.toString()))
                {
                    //ADD CALL TO FILTERED
                    filteredTransmitterGroups.add(hamnetCall);
                }*/
            }
            results.count = filteredTransmitterGroups.size();
            results.values = filteredTransmitterGroups;
        } else {
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.setmValues((ArrayList<TransmitterGroup>) results.values);
        //REFRESH
        adapter.notifyDataSetChanged();
    }
}
