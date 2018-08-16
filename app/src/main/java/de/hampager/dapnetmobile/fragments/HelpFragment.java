package de.hampager.dapnetmobile.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.hampager.dapnetmobile.R;


/**
 * A simple {@link Fragment} subclass
 * Use the {@link HelpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HelpFragment extends Fragment {


    public HelpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HelpFragment.
     */
    public static HelpFragment newInstance() {
        return new HelpFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_help, container, false);
        LinearLayout linearLayout = result.findViewById(R.id.helpLayout);
        for (String s : getResources().getStringArray(R.array.faq)) {
            TextView tv = new TextView(getActivity());
            tv.setText(Html.fromHtml(s));
            linearLayout.addView(tv);
        }
        return result;
    }

}
