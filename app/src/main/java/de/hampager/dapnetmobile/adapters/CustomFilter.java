package de.hampager.dapnetmobile.adapters;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

import de.hampager.dap4j.models.CallResource;

public class CustomFilter extends Filter {
    private CallAdapter adapter;
    private List<CallResource> filterList;
    public CustomFilter(List<CallResource> filterList,CallAdapter adapter)
    {
        this.adapter=adapter;
        this.filterList=filterList;
    }
    //FILTERING OCCURS
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results=new FilterResults();
        //CHECK CONSTRAINT VALIDITY
        if(constraint != null && constraint.length() > 0)
        {
            //CHANGE TO UPPER
            constraint=constraint.toString().toUpperCase();
            //STORE OUR FILTERED PLAYERS
            List<CallResource> filteredCallResources=new ArrayList<>();
            for (CallResource hamnetCall:filterList)
            {
                //CHECK

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
                    filteredCallResources.add(hamnetCall);
                }
            }
            results.count=filteredCallResources.size();
            results.values=filteredCallResources;
        }else
        {
            results.count=filterList.size();
            results.values=filterList;
        }
        return results;
    }
    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.setmValues((ArrayList<CallResource>) results.values);
        //REFRESH
        adapter.notifyDataSetChanged();
    }
}
