package de.hampager.dapnetmobile.filters;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

import de.hampager.dap4j.models.Rubric;
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
            //STORE OUR FILTERED ITEMS
            CharSequence constraintU=constraint.toString().toUpperCase();
            List<Transmitter> filteredCallSigns = new ArrayList<>();
            for (Transmitter transmitter : filterList) {
                //CHECK
                String name= transmitter.getName().toUpperCase();
                String address=transmitter.getAddress().getIpAddr().toUpperCase();
                String status=transmitter.getStatus().toUpperCase();
                String type = transmitter.getDeviceType().toUpperCase();
                List<String> ownerNames = transmitter.getOwnerNames();
                if(name.contains(constraintU)||address.contains(constraintU)||status.contains(constraintU)||type.contains(constraintU)||ownerNames.contains(constraint.toString()))
                {
                    //ADD CALL TO FILTERED
                    filteredCallSigns.add(transmitter);
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
        adapter.setmValues((ArrayList<Transmitter>) results.values);
        //REFRESH
        adapter.notifyDataSetChanged();
    }
}
