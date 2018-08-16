package de.hampager.dapnetmobile.filters;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

import de.hampager.dap4j.models.CallSign;
import de.hampager.dap4j.models.Pager;
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
            //STORE OUR FILTERED ITEMS
            List<CallSign> filteredCallSigns = new ArrayList<>();
            for (CallSign callSign : filterList) {
                //CHECK
                String desc= callSign.getDescription().toUpperCase();
                String name=callSign.getName().toUpperCase();
                List<String> callSignOwnerNames = callSign.getOwnerNames();
                List<String> pagerNames = new ArrayList<>();
                for (Pager p : callSign.getPagers()){
                    pagerNames.add(p.getName());
                }
                if(desc.contains(constraint.toString().toUpperCase())||name.contains(constraint.toString().toUpperCase())||callSignOwnerNames.contains(constraint.toString())||pagerNames.contains(constraint.toString()))
                {
                    //ADD CALL TO FILTERED
                    filteredCallSigns.add(callSign);
                }
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
