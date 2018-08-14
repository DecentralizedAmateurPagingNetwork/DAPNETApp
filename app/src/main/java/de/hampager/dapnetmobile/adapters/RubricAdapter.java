package de.hampager.dapnetmobile.adapters;

import android.support.annotation.NonNull;
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
import de.hampager.dapnetmobile.activites.MainActivity;
import de.hampager.dapnetmobile.R;
import de.hampager.dapnetmobile.filters.RubricFilter;
import de.hampager.dapnetmobile.fragments.TableFragment;

public class RubricAdapter extends RecyclerView.Adapter<RubricAdapter.TableViewHolder> implements Filterable {
    private List<Rubric> mValues;
    private List<Rubric> filterValues;
    private de.hampager.dapnetmobile.filters.RubricFilter rubricFilter;

    public RubricAdapter(List<Rubric> mValues) {
        this.mValues = mValues;
    }

    @NonNull
    @Override
    public RubricAdapter.TableViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_table_item_row, viewGroup, false);
        return new TableViewHolder(view);
    }

    //Write Content of Call Items for the RecyclerView
    @Override
    public void onBindViewHolder(@NonNull TableViewHolder viewHolder, int i) {
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
        if (rubricFilter == null) rubricFilter = new RubricFilter(mValues, this);
        return rubricFilter;
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
        return rubricFilter;
    }

    public void setRubricFilter(RubricFilter rubricFilter) {
        this.rubricFilter = rubricFilter;
    }

    //Holds relevant parts of one Call Item
    public class TableViewHolder extends RecyclerView.ViewHolder {
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("CallAdapter", "CLICK");
                //TODO: Inform fragment of rubricName
                ((MainActivity) v.getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.container, TableFragment.newInstance(TableFragment.TableTypes.RUBRIC_CONTENT)).addToBackStack("RUBRIC_CONTENT").commit();
            }
        };
        private TextView mUpperLeft;
        private TextView mLowerLeft;
        private TextView mCenter;
        private TextView mUpperRight;
        //Missing: mHiddenMore,mLowerRight,mHiddenCenter

        public TableViewHolder(View view) {
            super(view);

            mUpperRight = view.findViewById(R.id.table_upperRight);
            mUpperLeft = view.findViewById(R.id.table_upperLeft);
            mLowerLeft = view.findViewById(R.id.table_lowerLeft);
            mCenter = view.findViewById(R.id.table_center);
            view.setOnClickListener(mOnClickListener);
        }
    }
}
