package de.hampager.dapnetmobile.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
    private List<User> mValues, filterValues;
    private de.hampager.dapnetmobile.filters.UserFilter UserFilter;

    public UserAdapter(List<User> mValues) {
        this.mValues = mValues;
    }

    @Override
    public UserAdapter.TableViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_table_item_row, viewGroup, false);
        return new TableViewHolder(view);
    }

    //Write Content of Call Items for the RecyclerView
    @Override
    public void onBindViewHolder(TableViewHolder viewHolder, int i) {
        //TODO: ADAPT
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
        if (UserFilter == null) UserFilter = new UserFilter(mValues, this);
        return UserFilter;
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
        return UserFilter;
    }

    public void setUserFilter(UserFilter UserFilter) {
        this.UserFilter = UserFilter;
    }

    //Holds relevant parts of one Call Item
    public class TableViewHolder extends RecyclerView.ViewHolder {
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("CallAdapter", "CLICK");
                /*
                if (Build.VERSION.SDK_INT>15){
                    if(mCallCallSign.getMaxLines()==1){
                        mCallCallSign.setMaxLines(10);
                        mCallUser.setMaxLines(10);
                        mOwner.setVisibility(View.VISIBLE);
                    }else{
                        mCallCallSign.setMaxLines(1);
                        mCallUser.setMaxLines(1);
                        mOwner.setVisibility(View.GONE);
                    }
                }*/
            }
        };
        private TextView mUpperLeft;
        private TextView mLowerLeft;
        private TextView mCenter;
        private TextView mUpperRight;
        private TextView mHiddenMore;
        private TextView mLowerRight;
        private TextView mHiddenCenter;

        public TableViewHolder(View view) {
            super(view);

            mUpperRight = (TextView) view.findViewById(R.id.table_upperRight);
            mUpperLeft = (TextView) view.findViewById(R.id.table_upperLeft);
            mLowerLeft = (TextView) view.findViewById(R.id.table_lowerLeft);
            mCenter = (TextView) view.findViewById(R.id.table_center);
            mHiddenMore = (TextView) view.findViewById(R.id.table_hiddenMore);
            mLowerRight = (TextView) view.findViewById(R.id.table_lowerRight);
            mHiddenCenter = (TextView) view.findViewById(R.id.table_hiddenCenter);

            view.setOnClickListener(mOnClickListener);
        }
    }
}
