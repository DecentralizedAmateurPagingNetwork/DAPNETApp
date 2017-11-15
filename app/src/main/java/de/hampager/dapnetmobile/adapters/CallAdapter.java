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
        HamnetCall hamnetCall = mValues.get(i);
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("To: ");
        boolean first = true;
        for (String s : hamnetCall.getTransmitterGroupNames()){
            stringBuilder.append(s);
            stringBuilder.append(";");
        }
        viewHolder.mCallTransmitterGroup.setText(stringBuilder.toString());
        stringBuilder=new StringBuilder();
        stringBuilder.append("Groups: ");
        for(String s :hamnetCall.getCallSignNames()){
            stringBuilder.append(s);
            stringBuilder.append(";");
        }
        viewHolder.mCallCallSign.setText(stringBuilder);
        viewHolder.mCallMsgContent.setText(hamnetCall.getText());
        viewHolder.mTimestamp.setText(hamnetCall.getTimestamp());
        boolean showOwner=true;
        if (showOwner) {
            viewHolder.mOwner.setText(hamnetCall.getOwnerName());
            viewHolder.mOwner.setVisibility(View.VISIBLE);

        }
        viewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                if(Build.VERSION.SDK_INT>15){
                    if(viewHolder.mCallCallSign.getMaxLines()==1){
                        viewHolder.mCallCallSign.setMaxLines(0);
                        viewHolder.mCallTransmitterGroup.setMaxLines(0);
                        viewHolder.mOwner.setVisibility(View.VISIBLE);
                    }else {
                        viewHolder.mCallCallSign.setMaxLines(1);
                        viewHolder.mCallTransmitterGroup.setMaxLines(1);
                        if(!showOwner)
                            viewHolder.mOwner.setVisibility(View.GONE);
                    }
                }
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
        private TextView mTimestamp;
        private TextView mOwner;
        private ItemClickListener itemClickListener;
        public CallViewHolder(View view) {
            super(view);
            mCallCallSign = (TextView) view.findViewById(R.id.call_call_sign);
            mCallTransmitterGroup = (TextView) view.findViewById(R.id.call_transmitter_groups);
            mCallMsgContent = (TextView) view.findViewById(R.id.call_msg_content);
            mCallMoreCalls = (TextView) view.findViewById(R.id.call_more_calls);
            mTimestamp = (TextView) view.findViewById(R.id.timeStamp);
            mOwner = (TextView) view.findViewById(R.id.call_owner);
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
