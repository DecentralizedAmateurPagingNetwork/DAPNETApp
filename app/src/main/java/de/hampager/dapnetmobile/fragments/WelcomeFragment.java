package de.hampager.dapnetmobile.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import de.hampager.dap4j.DAPNET;
import de.hampager.dap4j.DapnetSingleton;
import de.hampager.dap4j.callbacks.DapnetListener;
import de.hampager.dap4j.callbacks.DapnetResponse;
import de.hampager.dap4j.models.Stats;
import de.hampager.dapnetmobile.R;
import de.hampager.dapnetmobile.listeners.FragmentInteractionListener;
import de.hampager.dapnetmobile.adapters.StatsAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WelcomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WelcomeFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String TAG = "WelcomeFragment";

    private static final boolean FAB_VISIBLE = true;
    private static final int TITLE_ID = R.string.dapnet;

    private FragmentInteractionListener mListener;

    List<CardView> listItems = new ArrayList<>();
    //ImageView muninImageView;
    ImageView logoImageView;
    private RecyclerView recyclerView;
    private FrameLayout mapFrameLayout;
    private Fragment mapFragment;
    private StatsAdapter adapter;

    private DAPNET dapnet = DapnetSingleton.getInstance().getDapnet();

    /** Required empty public constructor */
    public WelcomeFragment() { /* empty */ }

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
        logoImageView = v.findViewById(R.id.logo_imageview);
        recyclerView = v.findViewById(R.id.welcome_statslist);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        SharedPreferences sharedPref = this.getActivity().getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
        String server = sharedPref.getString("server", getResources().getString(R.string.ClearNetURL));

        /* note: stats graph no longer updates from site

        muninImageView = v.findViewById(R.id.statsImage);
        if (server.contains("ampr.org"))
            Picasso.with(muninImageView.getContext())
                    .load("http://db0sda.ampr.org/munin-cgi/munin-cgi-graph/db0sda.ampr.org/dapnet.db0sda.ampr.org/dapnet-week.png")
                    .into(muninImageView);
        else
            Picasso.with(muninImageView.getContext())
                    .load("https://www.afu.rwth-aachen.de/munin-cgi/munin-cgi-graph/db0sda.ampr.org/dapnet.db0sda.ampr.org/dapnet-week.png")
                    .into(muninImageView);
         */

        // Initialize map
        mapFrameLayout = v.findViewById(R.id.map_container);
        this.getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.map_container, MapFragment.newInstance(), "MapFragment")
                .commit();

        // Initialize stats table
        fetchJSON(server);
    }

    private void fetchJSON(String server) {
        dapnet.getStats(new DapnetListener<Stats>() {
            @Override
            public void onResponse(DapnetResponse<Stats> dapnetResponse) {
                if (dapnetResponse.isSuccessful()) {
                    Log.i(TAG, "Connection was successful");
                    // tasks available
                    Stats data = dapnetResponse.body();
                    adapter = new StatsAdapter(data);
                    recyclerView.setAdapter(adapter);

                }
                else {
                    Log.e(TAG, "Error.");
                    //TODO: implement .code, .message
                    /*
                    APIError error = ErrorUtils.parseError(response);
                    Log.e(TAG,error.toString());
                    Log.e(TAG, "Error " + response.code());
                    Log.e(TAG, response.message());
                    Snackbar.make(recyclerView, "Error! " + response.code() + " " + response.message(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                     */
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                // something went completely wrong (e.g. no internet connection)
                Log.e(TAG, "Fatal connection error.. " + throwable.getMessage());
                if (getActivity() != null && getActivity().findViewById(R.id.container) != null) {
                    Snackbar.make(getActivity().findViewById(R.id.container), "Fatal connection error.. " + throwable.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_welcome, container, false);

        //Implement arguments and bundle checks
        /*
        TextView description = v.findViewById(R.id.DAPscription);
        description.setText(Html.fromHtml(getResources().getString(R.string.DAPscription)));
        description.setMovementMethod(LinkMovementMethod.getInstance());
         */
        initViews(v);
        return v;
    }

    // OnWelcomeFragmentListener
    /**
     * Hides image logo and stats table for the map to take up most of the screen.
     *
     * @param full  flag for expanding the map throughout the view
     * @return full
     */
    public boolean setMapFull(boolean full) {
        recyclerView.setVisibility((full) ? View.GONE : View.VISIBLE);
        logoImageView.setVisibility((full) ? View.GONE : View.VISIBLE);
        return full;
    }

    // region for listener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

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
