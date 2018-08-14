package de.hampager.dapnetmobile.adapters;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.List;

import de.hampager.dap4j.models.CallResource;
import de.hampager.dapnetmobile.R;
import de.hampager.dapnetmobile.filters.CustomFilter;

public class CallAdapter extends RecyclerView.Adapter<CallAdapter.CallViewHolder> implements Filterable {
    private List<CallResource> mValues;
    private List<CallResource> filterValues;
    private CustomFilter customFilter;

    public CallAdapter(List<CallResource> mValues) {
        this.mValues = mValues;
    }

    @Override
    public CallAdapter.CallViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_call_item_row, viewGroup, false);
        return new CallViewHolder(view);
    }

    //Write Content of Call Items for the RecyclerView
    @Override
    public void onBindViewHolder(CallAdapter.CallViewHolder viewHolder, int i) {
        CallResource hamnetCall = mValues.get(i);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Groups: ");
        stringBuilder.append(hamnetCall.getTransmitterGroupNames().get(0));
        for (int j = 1; j < hamnetCall.getTransmitterGroupNames().size(); j++) {
            stringBuilder.append(", ");
            stringBuilder.append(hamnetCall.getTransmitterGroupNames().get(j));
        }
        viewHolder.mCallTransmitterGroup.setText(stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append("To: ");
        stringBuilder.append(hamnetCall.getCallSignNames().get(0).toUpperCase());
        for (int j = 1; j < hamnetCall.getCallSignNames().size(); j++) {
            stringBuilder.append(", ");
            stringBuilder.append(hamnetCall.getCallSignNames().get(j).toUpperCase());
        }
        viewHolder.mCallCallSign.setText(stringBuilder);
        viewHolder.mCallMsgContent.setText(hamnetCall.getText());
        viewHolder.mTimestamp.setText(hamnetCall.getTimestamp());
        viewHolder.mOwner.setText(hamnetCall.getOwnerName() + ":");
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public Filter getFilter() {
        if (customFilter == null) customFilter = new CustomFilter(mValues, this);
        return customFilter;
    }

    public List<CallResource> getmValues() {
        return mValues;
    }

    public void setmValues(List<CallResource> mValues) {
        this.mValues = mValues;
    }

    public List<CallResource> getFilterValues() {
        return filterValues;
    }

    public void setFilterValues(List<CallResource> filterValues) {
        this.filterValues = filterValues;
    }

    public CustomFilter getCustomFilter() {
        return customFilter;
    }

    public void setCustomFilter(CustomFilter customFilter) {
        this.customFilter = customFilter;
    }

    //Holds relevant parts of one Call Item
    public class CallViewHolder extends RecyclerView.ViewHolder {
        private TextView mCallCallSign;
        private TextView mCallTransmitterGroup;
        private TextView mCallMsgContent;
        //Missing: mCallMoreCalls
        private TextView mTimestamp;
        private TextView mOwner;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("CallAdapter", "CLICK");
                if (Build.VERSION.SDK_INT > 15) {
                    if (mCallCallSign.getMaxLines() == 1) {
                        mCallCallSign.setMaxLines(10);
                        mCallTransmitterGroup.setMaxLines(10);
                        mOwner.setVisibility(View.VISIBLE);
                    } else {
                        mCallCallSign.setMaxLines(1);
                        mCallTransmitterGroup.setMaxLines(1);
                        mOwner.setVisibility(View.GONE);
                    }
                }
            }
        };

        public CallViewHolder(View view) {
            super(view);
            mCallCallSign = (TextView) view.findViewById(R.id.table_upperLeft);
            mCallTransmitterGroup = (TextView) view.findViewById(R.id.table_lowerLeft);
            mCallMsgContent = (TextView) view.findViewById(R.id.table_center);
            mTimestamp = (TextView) view.findViewById(R.id.table_lowerRight);
            mOwner = (TextView) view.findViewById(R.id.table_hiddenCenter);
            view.setOnClickListener(mOnClickListener);
        }
    }
}
