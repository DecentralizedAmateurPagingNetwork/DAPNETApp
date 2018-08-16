package de.hampager.dapnetmobile.filters;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

import de.hampager.dap4j.models.Transmitter;
import de.hampager.dap4j.models.User;
import de.hampager.dapnetmobile.adapters.UserAdapter;

public class UserFilter extends Filter {
    private UserAdapter adapter;
    private List<User> filterList;

    public UserFilter(List<User> filterList, UserAdapter adapter) {
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
            List<User> filteredCallSigns = new ArrayList<>();
            for (User user : filterList) {
                //CHECK
                String name= user.getName().toUpperCase();
                if(name.contains(constraintU))
                {
                    //ADD CALL TO FILTERED
                    filteredCallSigns.add(user);
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
        adapter.setmValues((ArrayList<User>) results.values);
        //REFRESH
        adapter.notifyDataSetChanged();
    }
}
