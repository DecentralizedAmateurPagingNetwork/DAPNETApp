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

import de.hampager.dap4j.models.CallSign;
import de.hampager.dap4j.models.Pager;
import de.hampager.dapnetmobile.R;
import de.hampager.dapnetmobile.filters.SubscriberFilter;

public class SubscriberAdapter extends RecyclerView.Adapter<SubscriberAdapter.TableViewHolder> implements Filterable {
    private List<CallSign> mValues, filterValues;
    private de.hampager.dapnetmobile.filters.SubscriberFilter SubscriberFilter;

    public SubscriberAdapter(List<CallSign> mValues) {
        this.mValues = mValues;
    }

    @Override
    public SubscriberAdapter.TableViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_table_item_row, viewGroup, false);
        return new TableViewHolder(view);
    }

    //Write Content of Call Items for the RecyclerView
    @Override
    public void onBindViewHolder(TableViewHolder viewHolder, int i) {
        CallSign hamnetCall = mValues.get(i);
        /*

        StringBuilder upperLeft=new StringBuilder();
        upperLeft.append("To: ");
        upperLeft.append(hamnetCall.getCallSignNames().get(0).toUpperCase());
        for(int j=1;j<hamnetCall.getCallSignNames().size();j++){
            upperLeft.append(", ");
            upperLeft.append(hamnetCall.getCallSignNames().get(j).toUpperCase());
        }
        viewHolder.mCallCallSign.setText(upperLeft);
        viewHolder.mCallMsgContent.setText(hamnetCall.getText());
        viewHolder.mTimestamp.setText(hamnetCall.getTimestamp());
        viewHolder.mOwner.setText(hamnetCall.getOwnerName()+":");
*/
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
        } catch (Exception e) {
        }
        viewHolder.mLowerLeft.setText(lowerLeft.toString());
        try {
            viewHolder.mUpperRight.setText(hamnetCall.getOwnerNames().toString().toUpperCase());
        } catch (Exception e) {
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public Filter getFilter() {
        if (SubscriberFilter == null) SubscriberFilter = new SubscriberFilter(mValues, this);
        return SubscriberFilter;
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
        return SubscriberFilter;
    }

    public void setSubscriberFilter(SubscriberFilter SubscriberFilter) {
        this.SubscriberFilter = SubscriberFilter;
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
                        mCallTransmitterGroup.setMaxLines(10);
                        mOwner.setVisibility(View.VISIBLE);
                    }else{
                        mCallCallSign.setMaxLines(1);
                        mCallTransmitterGroup.setMaxLines(1);
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
