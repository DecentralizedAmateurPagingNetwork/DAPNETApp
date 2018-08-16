package de.hampager.dapnetmobile.filters;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

import de.hampager.dap4j.models.News;
import de.hampager.dap4j.models.Rubric;
import de.hampager.dapnetmobile.adapters.RubricContentAdapter;

public class RubricContentFilter extends Filter {
    private RubricContentAdapter adapter;
    private List<News> filterList;

    public RubricContentFilter(List<News> filterList, RubricContentAdapter adapter) {
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
            constraint=constraint.toString().toUpperCase();
            List<News> filteredCallSigns = new ArrayList<>();
            for (News news : filterList) {
                //CHECK
                String name=news.getOwnerName().toUpperCase();
                String desc=news.getText().toUpperCase();
                if(desc.contains(constraint)||name.contains(constraint))
                {
                    //ADD CALL TO FILTERED
                    filteredCallSigns.add(news);
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
        adapter.setmValues((ArrayList<News>) results.values);
        //REFRESH
        adapter.notifyDataSetChanged();
    }
}
