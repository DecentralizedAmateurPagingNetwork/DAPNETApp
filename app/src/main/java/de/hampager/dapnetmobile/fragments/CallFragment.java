package de.hampager.dapnetmobile.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import de.hampager.dapnetmobile.R;
import de.hampager.dapnetmobile.adapters.DataAdapter;
import de.hampager.dapnetmobile.api.HamPagerService;
import de.hampager.dapnetmobile.api.HamnetCall;
import de.hampager.dapnetmobile.api.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallFragment extends Fragment {
    private final String TAG = "CallFragment";
    private RecyclerView recyclerView;
    private DataAdapter adapter;

    public CallFragment() {
        // Required empty public constructor
    }

    public static CallFragment newInstance() {
        return new CallFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initViews(View v) {
        recyclerView = (RecyclerView) v.findViewById(R.id.item_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        SharedPreferences sharedPref = this.getActivity().getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
        String server = sharedPref.getString("server", null);
        String user = sharedPref.getString("user", null);
        String password = sharedPref.getString("pass", null);
        Boolean admin = sharedPref.getBoolean("admin", true);
        fetchJSON(server, user, password, admin);
    }

    private void fetchJSON(String server, String user, String password, boolean admin) {
        ServiceGenerator.changeApiBaseUrl(server);
        HamPagerService service = ServiceGenerator.createService(HamPagerService.class, user, password);
        Call<ArrayList<HamnetCall>> call;
        Log.i(TAG, "fetchJSON, admin: " + admin);
        if (admin) {
            Log.i(TAG, "Admin access granted. Fetching All Calls...");
            call = service.getAllHamnetCalls();
        } else {
            Log.i(TAG, "Admin access not granted. Fetching own Calls...");
            call = service.getOwnerHamnetCalls(user);
        }
        call.enqueue(new Callback<ArrayList<HamnetCall>>() {
            @Override
            public void onResponse(Call<ArrayList<HamnetCall>> call, Response<ArrayList<HamnetCall>> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "Connection was successful");
                    // tasks available
                    ArrayList<HamnetCall> data = response.body();
                    adapter = new DataAdapter(data);
                    recyclerView.setAdapter(adapter);
                } else {
                    //APIError error = ErrorUtils.parseError(response);
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
            public void onFailure(Call<ArrayList<HamnetCall>> call, Throwable t) {
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
        initViews(v);
        return v;
    }


}
