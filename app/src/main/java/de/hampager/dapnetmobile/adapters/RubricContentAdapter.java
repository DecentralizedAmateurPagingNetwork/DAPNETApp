package de.hampager.dapnetmobile.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.List;

import de.hampager.dap4j.models.News;
import de.hampager.dapnetmobile.R;
import de.hampager.dapnetmobile.filters.RubricContentFilter;

public class RubricContentAdapter extends RecyclerView.Adapter<RubricContentAdapter.TableViewHolder> implements Filterable {
    private static final String NULL = "Empty";
    private List<News> mValues;
    private List<News> filterValues;
    private de.hampager.dapnetmobile.filters.RubricContentFilter rubricContentFilter;

    public RubricContentAdapter(List<News> mValues) {
        this.mValues = mValues;
    }

    @NonNull
    @Override
    public RubricContentAdapter.TableViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_table_item_row, viewGroup, false);
        return new TableViewHolder(view);
    }

    //Write Content of Call Items for the RecyclerView
    @Override
    public void onBindViewHolder(@NonNull TableViewHolder viewHolder, int i) {
        //TODO: ADAPT
        News news = mValues.get(i);
        if(news!=null) {
            viewHolder.mUpperLeft.setText("Rubric: " + news.getRubricName());
            viewHolder.mCenter.setText(news.getText());
            viewHolder.mLowerRight.setText(news.getTimestamp());
            viewHolder.mLowerLeft.setText(news.getOwnerName());
        }else{
            viewHolder.mUpperLeft.setText(NULL);
            viewHolder.mCenter.setText(NULL);
            viewHolder.mLowerRight.setText(NULL);
            viewHolder.mLowerLeft.setText(NULL);
        }

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public Filter getFilter() {
        if (rubricContentFilter == null)
            rubricContentFilter = new RubricContentFilter(mValues, this);
        return rubricContentFilter;
    }

    public List<News> getmValues() {
        return mValues;
    }

    public void setmValues(List<News> mValues) {
        this.mValues = mValues;
    }

    public List<News> getFilterValues() {
        return filterValues;
    }

    public void setFilterValues(List<News> filterValues) {
        this.filterValues = filterValues;
    }

    public RubricContentFilter getRubricContentFilter() {
        return rubricContentFilter;
    }

    public void setRubricContentFilter(RubricContentFilter rubricContentFilter) {
        this.rubricContentFilter = rubricContentFilter;
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
        private TextView mLowerRight;
        //Missing: mUpperRight,mHiddenMore,mHiddenCenter

        public TableViewHolder(View view) {
            super(view);
            mUpperLeft = view.findViewById(R.id.table_upperLeft);
            mLowerLeft = view.findViewById(R.id.table_lowerLeft);
            mCenter = view.findViewById(R.id.table_center);
            mLowerRight = view.findViewById(R.id.table_lowerRight);
            view.setOnClickListener(mOnClickListener);
        }
    }
}
