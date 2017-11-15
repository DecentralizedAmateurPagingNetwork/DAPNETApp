package de.hampager.dapnetmobile.adapters;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

import de.hampager.dapnetmobile.api.HamnetCall;

public class CustomFilter extends Filter {
    private CallAdapter adapter;
    private List<HamnetCall> filterList;
    public CustomFilter(List<HamnetCall> filterList,CallAdapter adapter)
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
            ArrayList<HamnetCall> filteredHamnetCalls=new ArrayList<>();
            for (HamnetCall hamnetCall:filterList)
            {
                //CHECK

                String text = hamnetCall.getText().toUpperCase();
                List<String> callSignNames = hamnetCall.getCallSignNames();
                List<String> transmitterGroupNames = hamnetCall.getTransmitterGroupNames();
                String ownerName = hamnetCall.getOwnerName();

                if(text.contains(constraint)||ownerName.contains(constraint)||callSignNames.contains(constraint.toString())||transmitterGroupNames.contains(constraint.toString()))
                {
                    //ADD CALL TO FILTERED
                    filteredHamnetCalls.add(hamnetCall);
                }
            }
            results.count=filteredHamnetCalls.size();
            results.values=filteredHamnetCalls;
        }else
        {
            results.count=filterList.size();
            results.values=filterList;
        }
        return results;
    }
    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.setmValues((ArrayList<HamnetCall>) results.values);
        //REFRESH
        adapter.notifyDataSetChanged();
    }
}
