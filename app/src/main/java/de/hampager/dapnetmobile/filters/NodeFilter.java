package de.hampager.dapnetmobile.filters;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

import de.hampager.dap4j.models.Node;
import de.hampager.dapnetmobile.adapters.NodeAdapter;

public class NodeFilter extends Filter {
    private NodeAdapter adapter;
    private List<Node> filterList;

    public NodeFilter(List<Node> filterList, NodeAdapter adapter) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    //FILTERING OCCURS
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        //CHECK CONSTRAINT VALIDITY
        if (constraint != null && constraint.length() > 0) {
            //CHANGE TO UPPER
            constraint = constraint.toString().toUpperCase();
            //STORE OUR FILTERED PLAYERS
            List<Node> filteredNodes = new ArrayList<>();
            for (Node hamnetCall : filterList) {
                //CHECK
/*
                String text = hamnetCall.getText().toUpperCase();
                List<String> NodeNames = hamnetCall.getNodeNames();
                List<String> NodeNames = hamnetCall.getNodeNames();
                String ownerName = hamnetCall.getOwnerName();
                for(int i=0;i<NodeNames.size();i++){
                    NodeNames.set(i,NodeNames.get(i).toUpperCase());
                }
                for(int i=0;i<NodeNames.size();i++){
                    NodeNames.set(i,NodeNames.get(i).toUpperCase());
                }
                if(text.contains(constraint)||ownerName.contains(constraint)||NodeNames.contains(constraint.toString()))
                {
                    //ADD CALL TO FILTERED
                    filteredNodes.add(hamnetCall);
                }*/
            }
            results.count = filteredNodes.size();
            results.values = filteredNodes;
        } else {
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.setmValues((ArrayList<Node>) results.values);
        //REFRESH
        adapter.notifyDataSetChanged();
    }
}
