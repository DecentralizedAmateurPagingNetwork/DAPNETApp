package de.hampager.dapnetmobile.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hampager.dap4j.DAPNET;
import de.hampager.dap4j.DapnetSingleton;
import de.hampager.dap4j.callbacks.DapnetListener;
import de.hampager.dap4j.callbacks.DapnetResponse;
import de.hampager.dap4j.models.CallResource;
import de.hampager.dapnetmobile.R;
import de.hampager.dapnetmobile.listeners.FragmentInteractionListener;
import de.hampager.dapnetmobile.activites.MainActivity;
import de.hampager.dapnetmobile.adapters.CallAdapter;

public class CallFragment extends Fragment implements SearchView.OnQueryTextListener {
    private static final String TAG = "CallFragment";

    private static final boolean FAB_VISIBLE = true;
    private static final int TITLE_ID = R.string.calls;

    private CallAdapter adapter;
    private SwipeRefreshLayout mSwipe;
    private SearchView searchView;

    private FragmentInteractionListener mListener;

    /** Required public constructor */
    public CallFragment() { /* empty */ }

    public static CallFragment newInstance() {
        return new CallFragment();
    }

    private void initViews(View v) {
        adapter = new CallAdapter(new ArrayList<CallResource>());

        RecyclerView recyclerView = v.findViewById(R.id.item_recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager mLayoutManager
                = new LinearLayoutManager(Objects.requireNonNull(getActivity()).getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(mLayoutManager);

        ((MainActivity)getActivity()).setActionBarTitle("DAPNET calls");
        fetchJSON();
    }

    private void fetchJSON() {
        mSwipe.setRefreshing(true);
        DAPNET dapnet = DapnetSingleton.getInstance().getDapnet();
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
        String s = "";
        boolean admin = sharedPreferences.getBoolean("admin", false);
        if (!admin) {
            s = sharedPreferences.getString("user", "null");
        }
        if (admin) {
            Log.i(TAG, "Admin access granted. Fetching All Calls...");
        }
        dapnet.getCalls(s, new DapnetListener<List<CallResource>>() {
            @Override
            public void onResponse(DapnetResponse<List<CallResource>> dapnetResponse) {
                if (dapnetResponse.isSuccessful()) {
                    Log.i(TAG, "Connection was successful");
                    // tasks available
                    List<CallResource> data = dapnetResponse.body();
                    adapter.setmValues(data);
                    adapter.notifyDataSetChanged();
                }
                else {
                    Log.e(TAG, "Error");
                    //TODO: .code,.message etc
                }
                mSwipe.setRefreshing(false);
            }

            @Override
            public void onFailure(Throwable throwable) {
                // something went completely wrong (e.g. no internet connection)
                Log.e(TAG, throwable.getMessage());
                mSwipe.setRefreshing(false);
            }
        });

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_call, container, false);
        v.setTag(TAG);
        setHasOptionsMenu(true);
        mSwipe = v.findViewById(R.id.swipeRefreshCalls);
        initViews(v);

        /*
        // Define listener arguments
        if (mListener != null) {
            mListener.onFragmentInteraction(FAB_VISIBLE, TITLE_ID);
        }
        */

        // Setup refresh listener which triggers new data loading
        mSwipe.setOnRefreshListener(() -> {
            // Your code to refresh the list here. Make sure you call swipeContainer.setRefreshing(false)
            // once the network request has completed successfully.
            fetchJSON();
        });
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.i(TAG, "Creating menu...");
        inflater.inflate(R.menu.main_menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextChange(String query) {
        // Here is where we are going to implement the filter logic
        adapter.getFilter().filter(query);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    // region for listener
    @Override
    public void onStart() {
        super.onStart();
        try {
            mListener = (FragmentInteractionListener) getActivity();
            mListener.onFragmentInteraction(FAB_VISIBLE, TITLE_ID);
        }
        catch (ClassCastException cce) {
            Log.e(TAG, cce.getMessage());
            //throw new ClassCastException(getActivity().toString() + " must implement FragmentInteractionListener.");
        }
        catch (NullPointerException npe) {
            Log.e(TAG, npe.getMessage());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    // endregion for listener

}
