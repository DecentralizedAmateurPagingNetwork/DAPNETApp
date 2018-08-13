package de.hampager.dapnetmobile.filters;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

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
            //CHANGE TO UPPER
            constraint = constraint.toString().toUpperCase();
            //STORE OUR FILTERED PLAYERS
            List<User> filteredUsers = new ArrayList<>();
            for (User hamnetCall : filterList) {
                //CHECK
/*
                String text = hamnetCall.getText().toUpperCase();
                List<String> UserNames = hamnetCall.getUserNames();
                List<String> UserNames = hamnetCall.getUserNames();
                String ownerName = hamnetCall.getOwnerName();
                for(int i=0;i<UserNames.size();i++){
                    UserNames.set(i,UserNames.get(i).toUpperCase());
                }
                for(int i=0;i<UserNames.size();i++){
                    UserNames.set(i,UserNames.get(i).toUpperCase());
                }
                if(text.contains(constraint)||ownerName.contains(constraint)||UserNames.contains(constraint.toString()))
                {
                    //ADD CALL TO FILTERED
                    filteredUsers.add(hamnetCall);
                }*/
            }
            results.count = filteredUsers.size();
            results.values = filteredUsers;
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
