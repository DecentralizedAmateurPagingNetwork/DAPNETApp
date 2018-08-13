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

import de.hampager.dap4j.models.Rubric;
import de.hampager.dapnetmobile.R;
import de.hampager.dapnetmobile.filters.RubricFilter;

public class RubricAdapter extends RecyclerView.Adapter<RubricAdapter.TableViewHolder> implements Filterable {
    private List<Rubric> mValues, filterValues;
    private de.hampager.dapnetmobile.filters.RubricFilter RubricFilter;

    public RubricAdapter(List<Rubric> mValues) {
        this.mValues = mValues;
    }

    @Override
    public RubricAdapter.TableViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_table_item_row, viewGroup, false);
        return new TableViewHolder(view);
    }

    //Write Content of Call Items for the RecyclerView
    @Override
    public void onBindViewHolder(TableViewHolder viewHolder, int i) {
        //TODO: ADAPT
        Rubric hamnetRubric = mValues.get(i);

        StringBuilder groups = new StringBuilder();
        groups.append("Groups: ");
        groups.append(hamnetRubric.getTransmitterGroupNames().get(0).toUpperCase());
        for (int j = 1; j < hamnetRubric.getTransmitterGroupNames().size(); j++) {
            groups.append(", ");
            groups.append(hamnetRubric.getTransmitterGroupNames().get(j).toUpperCase());
        }
        StringBuilder lowerLeft = new StringBuilder();
        lowerLeft.append("Owner(s): ");
        lowerLeft.append(hamnetRubric.getOwnerNames().get(0));
        for (int j = 1; j < hamnetRubric.getTransmitterGroupNames().size(); j++) {
            groups.append(", ");
            groups.append(hamnetRubric.getTransmitterGroupNames().get(j).toUpperCase());
        }
        viewHolder.mUpperLeft.setText("Name: " + hamnetRubric.getName());
        viewHolder.mCenter.setText("Label: " + hamnetRubric.getLabel());
        viewHolder.mLowerLeft.setText(lowerLeft);
        viewHolder.mUpperRight.setText(groups);

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public Filter getFilter() {
        if (RubricFilter == null) RubricFilter = new RubricFilter(mValues, this);
        return RubricFilter;
    }

    public List<Rubric> getmValues() {
        return mValues;
    }

    public void setmValues(List<Rubric> mValues) {
        this.mValues = mValues;
    }

    public List<Rubric> getFilterValues() {
        return filterValues;
    }

    public void setFilterValues(List<Rubric> filterValues) {
        this.filterValues = filterValues;
    }

    public RubricFilter getRubricFilter() {
        return RubricFilter;
    }

    public void setRubricFilter(RubricFilter RubricFilter) {
        this.RubricFilter = RubricFilter;
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
