package de.hampager.dapnetmobile.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hampager.dapnetmobile.R;
import de.hampager.dapnetmobile.adapters.StatsAdapter;
import de.hampager.dapnetmobile.api.HamPagerService;
import de.hampager.dapnetmobile.api.ServiceGenerator;
import de.hampager.dapnetmobile.api.StatsResource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WelcomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WelcomeFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String TAG = "WelcomeFragment";
    ArrayList<CardView> listItems = new ArrayList<>();
    ImageView muninImageView;
    private RecyclerView recyclerView;
    private StatsAdapter adapter;
    private String server;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initViews(View v) {
        recyclerView = (RecyclerView) v.findViewById(R.id.welcome_statslist);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        SharedPreferences sharedPref = this.getActivity().getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
        server = sharedPref.getString("server", "http://www.hampager.de:8080");
        muninImageView = (ImageView) v.findViewById(R.id.statsImage);
        Picasso.with(muninImageView.getContext()).load("https://www.afu.rwth-aachen.de/munin/db0sda.ampr.org/dapnet.db0sda.ampr.org/dapnet-week.png").into(muninImageView);
        fetchJSON(server);
    }

    private void getImage() {

    }



    private void fetchJSON(String server) {

        try {
            ServiceGenerator.changeApiBaseUrl(server);
        } catch (java.lang.NullPointerException e) {
            ServiceGenerator.changeApiBaseUrl("http://www.hampager.de:8080");
        }
        HamPagerService service = ServiceGenerator.createService(HamPagerService.class);
        Call<StatsResource> call = service.getStats();
        call.enqueue(new Callback<StatsResource>() {
            @Override
            public void onResponse(Call<StatsResource> call, Response<StatsResource> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "Connection was successful");
                    // tasks available
                    StatsResource data = response.body();
                    adapter = new StatsAdapter(data);
                    recyclerView.setAdapter(adapter);

                } else {
                    //APIError error = ErrorUtils.parseError(response);
                    Log.e(TAG, "Error " + response.code());
                    Log.e(TAG, response.message());
                    Snackbar.make(recyclerView, "Error! " + response.code() + " " + response.message(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }

            @Override
            public void onFailure(Call<StatsResource> call, Throwable t) {
                // something went completely wrong (e.g. no internet connection)
                Log.e(TAG, t.getMessage());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_welcome, container, false);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            if (bundle.getBoolean(ARG_PARAM1, false)) {
                //TextView mLogInText = (TextView) v.findViewById(R.id.call_call_sign);
                //mLogInText.setText(R.string.welcome_loggedInText);
            }
        }
        TextView mLinkView1 = (TextView) v.findViewById(R.id.linkView1);
        mLinkView1.setMovementMethod(LinkMovementMethod.getInstance());
        TextView mLinkView2 = (TextView) v.findViewById(R.id.linkView2);
        mLinkView2.setMovementMethod(LinkMovementMethod.getInstance());

        initViews(v);
        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
