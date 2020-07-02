package de.hampager.dapnetmobile.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.hampager.dapnetmobile.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PrivacyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PrivacyFragment extends Fragment {
    // Views
    private LinearLayout privacyLinearLayout;

    /** Required empty public constructor */
    public PrivacyFragment() { /* empty */ }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PrivacyFragment.
     */
    public static PrivacyFragment newInstance() {
        return new PrivacyFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_privacy, container, false);

        // Create and populate LinearLayout
        privacyLinearLayout = view.findViewById(R.id.privacyLinearLayout);
        for (String s : getResources().getStringArray(R.array.privacy)) {
            TextView tv = new TextView(getActivity());
            tv.setText(Html.fromHtml(s));
            privacyLinearLayout.addView(tv);
        }
        return view;
    }

} // End of class PrivacyFragment
