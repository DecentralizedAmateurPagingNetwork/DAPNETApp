package de.hampager.dapnetmobile.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.List;

import de.hampager.dap4j.models.CallSign;
import de.hampager.dap4j.models.Pager;
import de.hampager.dapnetmobile.R;
import de.hampager.dapnetmobile.filters.SubscriberFilter;

public class SubscriberAdapter extends RecyclerView.Adapter<SubscriberAdapter.TableViewHolder> implements Filterable {
    private List<CallSign> mValues;
    private List<CallSign> filterValues;
    private de.hampager.dapnetmobile.filters.SubscriberFilter subscriberFilter;
    private static final String TAG = "SubscriberAdapter";

    public SubscriberAdapter(List<CallSign> mValues) {
        this.mValues = mValues;
    }

    @NonNull
    @Override
    public SubscriberAdapter.TableViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_table_item_row, viewGroup, false);
        return new TableViewHolder(view);
    }

    //Write Content of Call Items for the RecyclerView
    @Override
    public void onBindViewHolder(@NonNull TableViewHolder viewHolder, int i) {
        CallSign hamnetCall = mValues.get(i);
        viewHolder.mUpperLeft.setText(hamnetCall.getName().toUpperCase());
        viewHolder.mCenter.setText(hamnetCall.getDescription());
        StringBuilder lowerLeft = new StringBuilder();
        try {
            Pager temp = hamnetCall.getPagers().get(0);
            lowerLeft.append(temp.getName());
            lowerLeft.append("(");
            lowerLeft.append(temp.getNumber());
            lowerLeft.append(")");
            for (int j = 1; j < hamnetCall.getPagers().size(); j++) {
                lowerLeft.append(", ");
                temp = hamnetCall.getPagers().get(j);
                lowerLeft.append(temp.getName());
                lowerLeft.append("(");
                lowerLeft.append(temp.getNumber());
                lowerLeft.append(")");
            }
        }
        catch (Exception e) {
            Log.e(TAG,"Adapter error");
        }
        viewHolder.mLowerLeft.setText(lowerLeft.toString());
        try {
            viewHolder.mUpperRight.setText(hamnetCall.getOwnerNames().toString().toUpperCase());
        }
        catch (Exception e) {
            Log.e(TAG,"Adapter error, setting text failed");
        }
    }

    @Override
    public int getItemCount() {
        try {
            return mValues.size();
        }
        catch (Exception e) {
            Log.e(TAG,"Error filtering");
            return 0;
        }
    }

    @Override
    public Filter getFilter() {
        if (subscriberFilter == null) subscriberFilter = new SubscriberFilter(mValues, this);
        return subscriberFilter;
    }

    public List<CallSign> getmValues() {
        return mValues;
    }

    public void setmValues(List<CallSign> mValues) {
        this.mValues = mValues;
    }

    public List<CallSign> getFilterValues() {
        return filterValues;
    }

    public void setFilterValues(List<CallSign> filterValues) {
        this.filterValues = filterValues;
    }

    public SubscriberFilter getSubscriberFilter() {
        return subscriberFilter;
    }

    public void setSubscriberFilter(SubscriberFilter subscriberFilter) {
        this.subscriberFilter = subscriberFilter;
    }

    //Holds relevant parts of one Call Item
    public class TableViewHolder extends RecyclerView.ViewHolder {
        //OnClick Missing
        private TextView mUpperLeft;
        private TextView mLowerLeft;
        private TextView mCenter;
        private TextView mUpperRight;
        //Missing: mHiddenMore,mLowerRight,mHiddenCenter
        public TableViewHolder(View view) {
            super(view);
            mUpperRight = view.findViewById(R.id.table_upperRight);
            mUpperLeft = view.findViewById(R.id.table_upperLeft);
            mLowerLeft = view.findViewById(R.id.table_lowerLeft);
            mCenter = view.findViewById(R.id.table_center);
        }
    }
}
