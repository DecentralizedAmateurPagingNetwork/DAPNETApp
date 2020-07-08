package de.hampager.dapnetmobile.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.hampager.dapnetmobile.R;
import de.hampager.dapnetmobile.activites.MainActivity;
import de.hampager.dapnetmobile.activites.PrivacyActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PrivacyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PrivacyFragment extends Fragment {

    private ImageView logoImageView;
    private LinearLayout privacyLinearLayout;
    private Button acceptButton;

    private boolean fromLaunch;

    /** Required public constructor */
    public PrivacyFragment() { /* empty */ }

    /**
     * Use this factory method to create a new instance of this fragment using the provided parameters.
     *
     * @param fromLaunch  Flag for whether this is being launched from SplashActivity (true) or otherwise (false)
     * @return A new instance of fragment PrivacyFragment.
     */
    public static PrivacyFragment newInstance(boolean fromLaunch) {
        PrivacyFragment fragment = new PrivacyFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.fromLaunch = fromLaunch;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_privacy, container, false);

        privacyLinearLayout = view.findViewById(R.id.privacyLinearLayout);

        // Add ImageView dynamically
        logoImageView = new ImageView(getActivity());
        logoImageView.setImageResource(R.mipmap.ic_afu_dapnet_logo);
        privacyLinearLayout.addView(logoImageView);

        // Populate LinearLayout
        for (String s : getResources().getStringArray(R.array.privacy)) {
            TextView tv = new TextView(getActivity());
            tv.setText(Html.fromHtml(s));
            privacyLinearLayout.addView(tv);
        }

        // If fragment is being launched from SplashActivity
        if (fromLaunch) {
            // Add Button dynamically
            acceptButton = new Button(getActivity());
            acceptButton.setText(R.string.action_accept);
            acceptButton.setTextColor(getResources().getColor(R.color.white));
            acceptButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            acceptButton.setOnClickListener(v -> {
                // Save preferences
                SharedPreferences.Editor prefEditor = getActivity().getSharedPreferences(MainActivity.SP, Context.MODE_PRIVATE).edit();
                prefEditor.putBoolean("privacy_activity_executed", true);
                prefEditor.apply();

                // Send user to MainActivity
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, new WelcomeFragment()).addToBackStack("WELCOME").commit();
            });
            privacyLinearLayout.addView(acceptButton);
        }
        return view;
    }

} // End of class PrivacyFragment
