package de.hampager.dapnetmobile.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.List;

import de.hampager.dap4j.models.TransmitterGroup;
import de.hampager.dapnetmobile.R;
import de.hampager.dapnetmobile.filters.TransmitterGroupFilter;

public class TransmitterGroupAdapter extends RecyclerView.Adapter<TransmitterGroupAdapter.TableViewHolder> implements Filterable {
    private List<TransmitterGroup> mValues;
    private List<TransmitterGroup> filterValues;
    private de.hampager.dapnetmobile.filters.TransmitterGroupFilter transmitterGroupFilter;

    public TransmitterGroupAdapter(List<TransmitterGroup> mValues) {
        this.mValues = mValues;
    }

    @NonNull
    @Override
    public TransmitterGroupAdapter.TableViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_table_item_row, viewGroup, false);
        return new TableViewHolder(view);
    }

    //Write Content of Call Items for the RecyclerView
    @Override
    public void onBindViewHolder(@NonNull TableViewHolder viewHolder, int i) {
        //TODO: ADAPT
        TransmitterGroup hamnetTransmitterGroup = mValues.get(i);
        viewHolder.mUpperLeft.setText(hamnetTransmitterGroup.getName().toUpperCase());
        viewHolder.mUpperRight.setText(hamnetTransmitterGroup.getOwnerNames().toString());
        viewHolder.mCenter.setText(hamnetTransmitterGroup.getTransmitterNames().toString());
        viewHolder.mLowerLeft.setText(hamnetTransmitterGroup.getDescription());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public Filter getFilter() {
        if (transmitterGroupFilter == null)
            transmitterGroupFilter = new TransmitterGroupFilter(mValues, this);
        return transmitterGroupFilter;
    }

    public List<TransmitterGroup> getmValues() {
        return mValues;
    }

    public void setmValues(List<TransmitterGroup> mValues) {
        this.mValues = mValues;
    }

    public List<TransmitterGroup> getFilterValues() {
        return filterValues;
    }

    public void setFilterValues(List<TransmitterGroup> filterValues) {
        this.filterValues = filterValues;
    }

    public TransmitterGroupFilter getTransmitterGroupFilter() {
        return transmitterGroupFilter;
    }

    public void setTransmitterGroupFilter(TransmitterGroupFilter transmitterGroupFilter) {
        this.transmitterGroupFilter = transmitterGroupFilter;
    }

    //Holds relevant parts of one Call Item
    public class TableViewHolder extends RecyclerView.ViewHolder {
        private TextView mUpperLeft;
        private TextView mLowerLeft;
        private TextView mCenter;
        private TextView mUpperRight;
        //Missing:onclick,mHiddenMore,mLowerRight,mHiddenCenter

        public TableViewHolder(View view) {
            super(view);
            mUpperRight = view.findViewById(R.id.table_upperRight);
            mUpperLeft = view.findViewById(R.id.table_upperLeft);
            mLowerLeft = view.findViewById(R.id.table_lowerLeft);
            mCenter = view.findViewById(R.id.table_center);
        }
    }
}
