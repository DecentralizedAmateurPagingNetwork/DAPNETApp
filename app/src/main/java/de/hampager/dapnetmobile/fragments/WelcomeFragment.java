package de.hampager.dapnetmobile.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hampager.dap4j.APIError;
import de.hampager.dap4j.DAPNETAPI;
import de.hampager.dap4j.DapnetSingleton;
import de.hampager.dap4j.ErrorUtils;
import de.hampager.dap4j.models.Stats;
import de.hampager.dapnetmobile.R;
import de.hampager.dapnetmobile.adapters.StatsAdapter;

2.Call;
        2.Callback;
        2.Response;



/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WelcomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WelcomeFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String TAG = "WelcomeFragment";
    List<CardView> listItems = new ArrayList<>();
    ImageView muninImageView;
    private RecyclerView recyclerView;
    private StatsAdapter adapter;


    public WelcomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param loggedIn - whether user is logged in.
     * @return A new instance of fragment WelcomeFragment.
     */
    public static WelcomeFragment newInstance(Boolean loggedIn) {
        WelcomeFragment fragment = new WelcomeFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PARAM1, loggedIn);
        fragment.setArguments(args);

        return fragment;
    }


    private void initViews(View v) {
        recyclerView = (RecyclerView) v.findViewById(R.id.welcome_statslist);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        SharedPreferences sharedPref = this.getActivity().getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
        String server = sharedPref.getString("server", "http://www.hampager.de:8080");
        muninImageView = (ImageView) v.findViewById(R.id.statsImage);
        if (server.contains("ampr.org"))
            Picasso.with(muninImageView.getContext()).load("http://db0sda.ampr.org/munin-cgi/munin-cgi-graph/db0sda.ampr.org/dapnet.db0sda.ampr.org/dapnet-week.png").into(muninImageView);
        else
            Picasso.with(muninImageView.getContext()).load("https://www.afu.rwth-aachen.de/munin-cgi/munin-cgi-graph/db0sda.ampr.org/dapnet.db0sda.ampr.org/dapnet-week.png").into(muninImageView);
        fetchJSON(server);
    }



    private void fetchJSON(String server) {
        DAPNETAPI service = DapnetSingleton.getInstance().getService();
        Call<Stats> call = service.getStats();
        call.enqueue(new Callback<Stats>() {
            @Override
            public void onResponse(Call<Stats> call, Response<Stats> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "Connection was successful");
                    // tasks available
                    Stats data = response.body();
                    adapter = new StatsAdapter(data);
                    recyclerView.setAdapter(adapter);

                } else {
                    APIError error = ErrorUtils.parseError(response);
                    Log.e(TAG,error.toString());
                    Log.e(TAG, "Error " + response.code());
                    Log.e(TAG, response.message());
                    Snackbar.make(recyclerView, "Error! " + response.code() + " " + response.message(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }

            @Override
            public void onFailure(Call<Stats> call, Throwable t) {
                // something went completely wrong (e.g. no internet connection)
                Log.e(TAG, "Fatal connection error.. "+t.getMessage());
                if(getActivity()!=null&&getActivity().findViewById(R.id.container)!=null) {
                    Snackbar.make(getActivity().findViewById(R.id.container), "Fatal connection error.. " + t.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_welcome, container, false);

        //Implement arguments and bundle checks
        TextView description = (TextView) v.findViewById(R.id.DAPscription);
        description.setText( Html.fromHtml(getResources().getString(R.string.DAPscription)));
        description.setMovementMethod(LinkMovementMethod.getInstance());
        initViews(v);
        return v;
    }


}
