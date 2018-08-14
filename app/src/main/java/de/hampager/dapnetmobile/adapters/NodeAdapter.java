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

import de.hampager.dap4j.models.Node;
import de.hampager.dapnetmobile.R;
import de.hampager.dapnetmobile.filters.NodeFilter;

public class NodeAdapter extends RecyclerView.Adapter<NodeAdapter.TableViewHolder> implements Filterable {
    private List<Node> mValues;
    private List<Node> filterValues;
    private de.hampager.dapnetmobile.filters.NodeFilter nodeFilter;

    public NodeAdapter(List<Node> mValues) {
        this.mValues = mValues;
    }

    @Override
    public NodeAdapter.TableViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_table_item_row, viewGroup, false);
        return new TableViewHolder(view);
    }

    //Write Content of Call Items for the RecyclerView
    @Override
    public void onBindViewHolder(TableViewHolder viewHolder, int i) {
        Node hamnetNode = mValues.get(i);
        viewHolder.mUpperLeft.setText(hamnetNode.getName().toUpperCase());
        if (hamnetNode.getOwnerNames() != null)
            viewHolder.mUpperRight.setText(hamnetNode.getOwnerNames().toString());
        viewHolder.mLowerLeft.setText(hamnetNode.getStatus());
        if (hamnetNode.getAddress() != null)
            viewHolder.mCenter.setText(hamnetNode.getAddress().toString());
        else
            viewHolder.mCenter.setText("Address unavailable");
        if (hamnetNode.getVersion() != null)
            viewHolder.mLowerRight.setText(hamnetNode.getVersion());

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public Filter getFilter() {
        if (nodeFilter == null) nodeFilter = new NodeFilter(mValues, this);
        return nodeFilter;
    }

    public List<Node> getmValues() {
        return mValues;
    }

    public void setmValues(List<Node> mValues) {
        this.mValues = mValues;
    }

    public List<Node> getFilterValues() {
        return filterValues;
    }

    public void setFilterValues(List<Node> filterValues) {
        this.filterValues = filterValues;
    }

    public NodeFilter getNodeFilter() {
        return nodeFilter;
    }

    public void setNodeFilter(NodeFilter nodeFilter) {
        this.nodeFilter = nodeFilter;
    }

    //Holds relevant parts of one Call Item
    public class TableViewHolder extends RecyclerView.ViewHolder {
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("CallAdapter", "CLICK");
            }
        };
        private TextView mUpperLeft;
        private TextView mLowerLeft;
        private TextView mCenter;
        private TextView mUpperRight;
        private TextView mLowerRight;
        //Missing: mHiddenMore,mHiddenCenter

        public TableViewHolder(View view) {
            super(view);
            mUpperRight = view.findViewById(R.id.table_upperRight);
            mUpperLeft = view.findViewById(R.id.table_upperLeft);
            mLowerLeft = view.findViewById(R.id.table_lowerLeft);
            mCenter = view.findViewById(R.id.table_center);
            mLowerRight = view.findViewById(R.id.table_lowerRight);
            view.setOnClickListener(mOnClickListener);
        }
    }
}
