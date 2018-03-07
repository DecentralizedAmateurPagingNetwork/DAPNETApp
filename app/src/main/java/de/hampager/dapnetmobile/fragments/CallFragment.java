package de.hampager.dapnetmobile.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import de.hampager.dapnetmobile.R;
import de.hampager.dapnetmobile.adapters.CallAdapter;
import de.hampager.dap4j.DAPNETAPI;
import de.hampager.dap4j.models.CallResource;
import de.hampager.dap4j.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallFragment extends Fragment implements SearchView.OnQueryTextListener {
    private static final String TAG = "CallFragment";
    private RecyclerView recyclerView;
    private CallAdapter adapter;
    private SwipeRefreshLayout mSwipe;
    private String server;
    private String user;
    private String password;
    private Boolean admin;
    private SearchView searchView;
    public CallFragment() {
        // Required empty public constructor
    }

    public static CallFragment newInstance() {
        return new CallFragment();
    }


    private void initViews(View v) {
        adapter=new CallAdapter(new ArrayList<CallResource>());
        recyclerView = (RecyclerView) v.findViewById(R.id.item_recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);

        SharedPreferences sharedPref = this.getActivity().getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
        server = sharedPref.getString("server", "http://www.hampager.de:8080");
        user = sharedPref.getString("user", "invalid");
        password = sharedPref.getString("pass", "invalid");
        admin = sharedPref.getBoolean("admin", true);
        fetchJSON(server, user, password, admin);

    }

    private void fetchJSON(String server, String user, String password, boolean admin) {
        try {
            ServiceGenerator.changeApiBaseUrl(server);
        } catch (java.lang.NullPointerException e) {
            ServiceGenerator.changeApiBaseUrl("http://www.hampager.de:8080");
        }
        DAPNETAPI service = ServiceGenerator.createService(DAPNETAPI.class, user, password);
        Call<List<CallResource>> call;
        Log.i(TAG, "fetchJSON, admin: " + admin);
        if (admin) {
            Log.i(TAG, "Admin access granted. Fetching All Calls...");
            call = service.getCalls("");
        } else {
            Log.i(TAG, "Admin access not granted. Fetching own Calls...");
            call = service.getCalls(user);
        }
        call.enqueue(new Callback<List<CallResource>>() {
            @Override
            public void onResponse(Call<List<CallResource>> call, Response<List<CallResource>> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "Connection was successful");
                    // tasks available
                    List<CallResource> data = response.body();
                    adapter.setmValues(data);
                    adapter.notifyDataSetChanged();
                    mSwipe.setRefreshing(false);
                } else {
                    Log.e(TAG, "Error " + response.code());
                    Log.e(TAG, response.message());
                    Snackbar.make(recyclerView, "Error! " + response.code() + " " + response.message(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    if (response.code() == 401) {
                        SharedPreferences sharedPref = getActivity().getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.clear();
                        editor.apply();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<CallResource>> call, Throwable t) {
                // something went completely wrong (e.g. no internet connection)
                Log.e(TAG, t.getMessage());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_call, container, false);
        v.setTag(TAG);
        setHasOptionsMenu(true);
        initViews(v);
        mSwipe = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshCalls);

        // Setup refresh listener which triggers new data loading

        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override

            public void onRefresh() {

                // Your code to refresh the list here.

                // Make sure you call swipeContainer.setRefreshing(false)

                // once the network request has completed successfully.

                fetchJSON(server, user, password, admin);

            }

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

}
