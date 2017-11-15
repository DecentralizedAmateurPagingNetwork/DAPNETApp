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

import de.hampager.dapnetmobile.ItemClickListener;
import de.hampager.dapnetmobile.R;
import de.hampager.dapnetmobile.api.HamnetCall;

public class CallAdapter extends RecyclerView.Adapter<CallAdapter.CallViewHolder> implements Filterable{
    private List<HamnetCall> mValues,filterValues;
    private CustomFilter customFilter;
    public CallAdapter(List<HamnetCall> mValues) {
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
        viewHolder.mCallTransmitterGroup.setText(mValues.get(i).getTransmitterGroupNames().toString());
        viewHolder.mCallMsgContent.setText(mValues.get(i).getText());
        viewHolder.mCallCallSign.setText(mValues.get(i).getCallSignNames().get(0));
        if (mValues.get(i).getCallSignNames().size() > 1) {
            viewHolder.mCallMoreCalls.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mCallMoreCalls.setVisibility(View.INVISIBLE);
        }
        viewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                Log.d("CallAdapter","Item clicked");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
    @Override
    public Filter getFilter(){
        if (customFilter==null) customFilter=new CustomFilter(mValues,this);
        return customFilter;
    }
    //Holds relevant parts of one Call Item
    public class CallViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
        private TextView mCallCallSign;
        private TextView mCallTransmitterGroup;
        private TextView mCallMsgContent;
        private TextView mCallMoreCalls;
        private ItemClickListener itemClickListener;
        public CallViewHolder(View view) {
            super(view);
            mCallCallSign = (TextView) view.findViewById(R.id.call_call_sign);
            mCallTransmitterGroup = (TextView) view.findViewById(R.id.call_transmitter_groups);
            mCallMsgContent = (TextView) view.findViewById(R.id.call_msg_content);
            mCallMoreCalls = (TextView) view.findViewById(R.id.call_more_calls);
        }
        @Override
        public void onClick(View v) {
            this.itemClickListener.onItemClick(v,getLayoutPosition());
        }
        public void setItemClickListener(ItemClickListener itemClickListener){
            this.itemClickListener=itemClickListener;
        }
    }

    public List<HamnetCall> getmValues() {
        return mValues;
    }

    public void setmValues(List<HamnetCall> mValues) {
        this.mValues = mValues;
    }

    public List<HamnetCall> getFilterValues() {
        return filterValues;
    }

    public void setFilterValues(List<HamnetCall> filterValues) {
        this.filterValues = filterValues;
    }

    public CustomFilter getCustomFilter() {
        return customFilter;
    }

    public void setCustomFilter(CustomFilter customFilter) {
        this.customFilter = customFilter;
    }
}
