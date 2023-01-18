package de.hampager.dapnetmobile.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.List;

import de.hampager.dap4j.models.User;
import de.hampager.dapnetmobile.R;
import de.hampager.dapnetmobile.filters.UserFilter;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.TableViewHolder> implements Filterable {
    private List<User> mValues;
    private List<User> filterValues;
    private de.hampager.dapnetmobile.filters.UserFilter userFilter;

    public UserAdapter(List<User> mValues) {
        this.mValues = mValues;
    }

    @NonNull
    @Override
    public UserAdapter.TableViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_table_item_row, viewGroup, false);
        return new TableViewHolder(view);
    }

    //Write Content of Call Items for the RecyclerView
    @Override
    public void onBindViewHolder(@NonNull TableViewHolder viewHolder, int i) {
        User hamnetUser = mValues.get(i);
        viewHolder.mUpperLeft.setText("Admin: " + hamnetUser.getAdmin().toString().toUpperCase());
        viewHolder.mCenter.setText(hamnetUser.getName());
        viewHolder.mUpperRight.setText(hamnetUser.getMail());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public Filter getFilter() {
        if (userFilter == null) userFilter = new UserFilter(mValues, this);
        return userFilter;
    }

    public List<User> getmValues() {
        return mValues;
    }

    public void setmValues(List<User> mValues) {
        this.mValues = mValues;
    }

    public List<User> getFilterValues() {
        return filterValues;
    }

    public void setFilterValues(List<User> filterValues) {
        this.filterValues = filterValues;
    }

    public UserFilter getUserFilter() {
        return userFilter;
    }

    public void setUserFilter(UserFilter userFilter) {
        this.userFilter = userFilter;
    }

    //Holds relevant parts of one Call Item
    public class TableViewHolder extends RecyclerView.ViewHolder {
        //Missing: onClick
        private TextView mUpperLeft;
        private TextView mCenter;
        private TextView mUpperRight;
        //Missing:mLowerLeft,mHiddenMore,mLowerRight
        public TableViewHolder(View view) {
            super(view);
            mUpperRight = view.findViewById(R.id.table_upperRight);
            mUpperLeft = view.findViewById(R.id.table_upperLeft);
            mCenter = view.findViewById(R.id.table_center);
        }
    }
}
