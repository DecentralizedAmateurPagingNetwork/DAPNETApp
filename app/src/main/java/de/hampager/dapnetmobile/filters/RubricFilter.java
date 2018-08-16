package de.hampager.dapnetmobile.filters;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

import de.hampager.dap4j.models.CallSign;
import de.hampager.dap4j.models.Pager;
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
            //STORE OUR FILTERED ITEMS
            List<Rubric> filteredCallSigns = new ArrayList<>();
            for (Rubric rubric : filterList) {
                //CHECK
                String desc= rubric.getLabel().toUpperCase();
                String name=rubric.getName().toUpperCase();
                List<String> ownerNames = rubric.getOwnerNames();
                List<String> groups = rubric.getTransmitterGroupNames();
                if(desc.contains(constraint.toString().toUpperCase())||name.contains(constraint.toString().toUpperCase())||ownerNames.contains(constraint.toString())||groups.contains(constraint.toString()))
                {
                    //ADD CALL TO FILTERED
                    filteredCallSigns.add(rubric);
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
        adapter.setmValues((ArrayList<Rubric>) results.values);
        //REFRESH
        adapter.notifyDataSetChanged();
    }
}
