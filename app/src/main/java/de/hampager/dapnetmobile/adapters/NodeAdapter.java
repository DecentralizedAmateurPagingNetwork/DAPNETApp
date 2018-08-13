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
    private List<Node> mValues, filterValues;
    private de.hampager.dapnetmobile.filters.NodeFilter NodeFilter;

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
        //TODO: ADAPT
        Node hamnetNode = mValues.get(i);
        viewHolder.mUpperLeft.setText(hamnetNode.getName().toUpperCase());
        if (hamnetNode.getOwner() != null)
            viewHolder.mUpperRight.setText(hamnetNode.getOwner().toString());
        viewHolder.mLowerLeft.setText(hamnetNode.getStatus());
        if (hamnetNode.getAddress() != null)
            viewHolder.mCenter.setText(hamnetNode.getAddress().getIpAddr());
        else
            viewHolder.mCenter.setText("Address null");
        viewHolder.mLowerRight.setText(hamnetNode.getVersion());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public Filter getFilter() {
        if (NodeFilter == null) NodeFilter = new NodeFilter(mValues, this);
        return NodeFilter;
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
        return NodeFilter;
    }

    public void setNodeFilter(NodeFilter NodeFilter) {
        this.NodeFilter = NodeFilter;
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
                        mCallNode.setMaxLines(10);
                        mOwner.setVisibility(View.VISIBLE);
                    }else{
                        mCallCallSign.setMaxLines(1);
                        mCallNode.setMaxLines(1);
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
