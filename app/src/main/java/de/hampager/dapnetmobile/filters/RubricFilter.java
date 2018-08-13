package de.hampager.dapnetmobile.filters;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

import de.hampager.dap4j.models.Rubric;
import de.hampager.dapnetmobile.adapters.RubricAdapter;

public class RubricFilter extends Filter {
    private RubricAdapter adapter;
    private List<Rubric> filterList;

    public RubricFilter(List<Rubric> filterList, RubricAdapter adapter) {
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
            List<Rubric> filteredRubrics = new ArrayList<>();
            for (Rubric hamnetCall : filterList) {
                //CHECK
/*
                String text = hamnetCall.getText().toUpperCase();
                List<String> RubricNames = hamnetCall.getRubricNames();
                List<String> transmitterGroupNames = hamnetCall.getTransmitterGroupNames();
                String ownerName = hamnetCall.getOwnerName();
                for(int i=0;i<RubricNames.size();i++){
                    RubricNames.set(i,RubricNames.get(i).toUpperCase());
                }
                for(int i=0;i<transmitterGroupNames.size();i++){
                    transmitterGroupNames.set(i,transmitterGroupNames.get(i).toUpperCase());
                }
                if(text.contains(constraint)||ownerName.contains(constraint)||RubricNames.contains(constraint.toString()))
                {
                    //ADD CALL TO FILTERED
                    filteredRubrics.add(hamnetCall);
                }*/
            }
            results.count = filteredRubrics.size();
            results.values = filteredRubrics;
        } else {
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.setmValues((ArrayList<Rubric>) results.values);
        //REFRESH
        adapter.notifyDataSetChanged();
    }
}
