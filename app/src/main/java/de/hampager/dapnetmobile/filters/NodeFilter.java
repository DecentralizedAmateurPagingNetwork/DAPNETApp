package de.hampager.dapnetmobile.filters;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

import de.hampager.dap4j.models.Node;
import de.hampager.dap4j.models.TransmitterGroup;
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
            //STORE OUR FILTERED ITEMS
            CharSequence constraintU=constraint.toString().toUpperCase();
            List<Node> filteredCallSigns = new ArrayList<>();
            for (Node node : filterList) {
                //CHECK
                String name= node.getName().toUpperCase();
                String desc=node.getAddress().toString().toUpperCase();
                String status=node.getStatus().toUpperCase();
                String version=node.getVersion().toUpperCase();
                List<String> ownerNames = node.getOwnerNames();
                if(name.contains(constraintU)||(desc!=null&&desc.contains(constraintU))||status.contains(constraintU)||(version!=null&&version.contains(constraintU))||ownerNames.contains(constraint.toString()))
                {
                    //ADD CALL TO FILTERED
                    filteredCallSigns.add(node);
                }
            }
            results.count = filteredCallSigns.size();
            results.values = filteredCallSigns;
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
