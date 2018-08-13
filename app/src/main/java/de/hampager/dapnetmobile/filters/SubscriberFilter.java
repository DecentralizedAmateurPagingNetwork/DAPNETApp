package de.hampager.dapnetmobile.filters;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

import de.hampager.dap4j.models.CallSign;
import de.hampager.dapnetmobile.adapters.SubscriberAdapter;

public class SubscriberFilter extends Filter {
    private SubscriberAdapter adapter;
    private List<CallSign> filterList;

    public SubscriberFilter(List<CallSign> filterList, SubscriberAdapter adapter) {
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
            List<CallSign> filteredCallSigns = new ArrayList<>();
            for (CallSign hamnetCall : filterList) {
                //CHECK
/*
                String text = hamnetCall.getText().toUpperCase();
                List<String> callSignNames = hamnetCall.getCallSignNames();
                List<String> transmitterGroupNames = hamnetCall.getTransmitterGroupNames();
                String ownerName = hamnetCall.getOwnerName();
                for(int i=0;i<callSignNames.size();i++){
                    callSignNames.set(i,callSignNames.get(i).toUpperCase());
                }
                for(int i=0;i<transmitterGroupNames.size();i++){
                    transmitterGroupNames.set(i,transmitterGroupNames.get(i).toUpperCase());
                }
                if(text.contains(constraint)||ownerName.contains(constraint)||callSignNames.contains(constraint.toString()))
                {
                    //ADD CALL TO FILTERED
                    filteredCallSigns.add(hamnetCall);
                }*/
            }
            results.count = filteredCallSigns.size();
            results.values = filteredCallSigns;
        } else {
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.setmValues((ArrayList<CallSign>) results.values);
        //REFRESH
        adapter.notifyDataSetChanged();
    }
}
