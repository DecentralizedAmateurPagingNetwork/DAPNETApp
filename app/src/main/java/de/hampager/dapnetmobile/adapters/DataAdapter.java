package de.hampager.dapnetmobile.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import de.hampager.dapnetmobile.R;
import de.hampager.dapnetmobile.api.HamnetCall;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    private ArrayList<HamnetCall> mValues;

    public DataAdapter(ArrayList<HamnetCall> mValues) {
        this.mValues = mValues;
    }

    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_call_item_row, viewGroup, false);
        return new ViewHolder(view);
    }

    //Write Content of Call Items for the RecyclerView
    @Override
    public void onBindViewHolder(DataAdapter.ViewHolder viewHolder, int i) {
        viewHolder.mCallTransmitterGroup.setText(mValues.get(i).getTransmitterGroupNames().toString());
        viewHolder.mCallMsgContent.setText(mValues.get(i).getText());
        viewHolder.mCallCallSign.setText(mValues.get(i).getCallSignNames().get(0));
        if (mValues.get(i).getCallSignNames().size() > 1) {
            viewHolder.mCallMoreCalls.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mCallMoreCalls.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    //Holds relevant parts of one Call Item
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mCallCallSign, mCallTransmitterGroup, mCallMsgContent, mCallMoreCalls;

        public ViewHolder(View view) {
            super(view);
            mCallCallSign = (TextView) view.findViewById(R.id.call_call_sign);
            mCallTransmitterGroup = (TextView) view.findViewById(R.id.call_transmitter_groups);
            mCallMsgContent = (TextView) view.findViewById(R.id.call_msg_content);
            mCallMoreCalls = (TextView) view.findViewById(R.id.call_more_calls);
        }
    }

}
