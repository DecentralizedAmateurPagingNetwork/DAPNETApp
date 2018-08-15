package de.hampager.dapnetmobile.filters;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

import de.hampager.dap4j.models.Transmitter;
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
            //STORE OUR FILTERED ITEMS
            CharSequence constraintU=constraint.toString().toUpperCase();
            List<TransmitterGroup> filteredCallSigns = new ArrayList<>();
            for (TransmitterGroup transmitterGroup : filterList) {
                //CHECK
                String name= transmitterGroup.getName().toUpperCase();
                String desc=transmitterGroup.getDescription();
                List<String> ownerNames = transmitterGroup.getOwnerNames();
                List<String> transmitterNames=transmitterGroup.getTransmitterNames();
                if(name.contains(constraintU)||desc.contains(constraintU)||ownerNames.contains(constraint.toString())||transmitterNames.contains(constraint.toString()))
                {
                    //ADD CALL TO FILTERED
                    filteredCallSigns.add(transmitterGroup);
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
        adapter.setmValues((ArrayList<TransmitterGroup>) results.values);
        //REFRESH
        adapter.notifyDataSetChanged();
    }
}
