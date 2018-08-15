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

import de.hampager.dap4j.models.Transmitter;
import de.hampager.dapnetmobile.R;
import de.hampager.dapnetmobile.filters.TransmitterFilter;

public class TransmitterAdapter extends RecyclerView.Adapter<TransmitterAdapter.TableViewHolder> implements Filterable {
    private List<Transmitter> mValues;
    private List<Transmitter> filterValues;
    private de.hampager.dapnetmobile.filters.TransmitterFilter transmitterFilter;

    public TransmitterAdapter(List<Transmitter> mValues) {
        this.mValues = mValues;
    }

    @NonNull
    @Override
    public TransmitterAdapter.TableViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_table_item_row, viewGroup, false);
        return new TableViewHolder(view);
    }

    //Write Content of Call Items for the RecyclerView
    @Override
    public void onBindViewHolder(@NonNull TableViewHolder viewHolder, int i) {
        //TODO: ADAPT
        Transmitter hamnetTransmitter = mValues.get(i);
        viewHolder.mCenter.setText(hamnetTransmitter.getDeviceType() + " " + hamnetTransmitter.getDeviceVersion());
        viewHolder.mUpperLeft.setText(hamnetTransmitter.getOwnerNames().toString());
        if (hamnetTransmitter.getAddress() != null && hamnetTransmitter.getAddress().getIpAddr() != null)
            viewHolder.mUpperRight.setText(hamnetTransmitter.getAddress().getIpAddr());
        viewHolder.mLowerLeft.setText(hamnetTransmitter.getStatus());
    }

    @Override
    public int getItemCount() {
        try {return mValues.size();}
        catch (Exception e) {return 0;}
    }

    @Override
    public Filter getFilter() {
        if (transmitterFilter == null) transmitterFilter = new TransmitterFilter(mValues, this);
        return transmitterFilter;
    }

    public List<Transmitter> getmValues() {
        return mValues;
    }

    public void setmValues(List<Transmitter> mValues) {
        this.mValues = mValues;
    }

    public List<Transmitter> getFilterValues() {
        return filterValues;
    }

    public void setFilterValues(List<Transmitter> filterValues) {
        this.filterValues = filterValues;
    }

    public TransmitterFilter getTransmitterFilter() {
        return transmitterFilter;
    }

    public void setTransmitterFilter(TransmitterFilter transmitterFilter) {
        this.transmitterFilter = transmitterFilter;
    }

    //Holds relevant parts of one Call Item
    public class TableViewHolder extends RecyclerView.ViewHolder {
//Missing: onClick
        private TextView mUpperLeft;
        private TextView mLowerLeft;
        private TextView mCenter;
        private TextView mUpperRight;
        //Missing: mHiddenMore

        public TableViewHolder(View view) {
            super(view);

            mUpperRight = view.findViewById(R.id.table_upperRight);
            mUpperLeft = view.findViewById(R.id.table_upperLeft);
            mLowerLeft = view.findViewById(R.id.table_lowerLeft);
            mCenter = view.findViewById(R.id.table_center);
        }
    }
}
