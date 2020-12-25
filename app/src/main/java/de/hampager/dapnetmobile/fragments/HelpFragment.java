package de.hampager.dapnetmobile.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.hampager.dapnetmobile.R;
import de.hampager.dapnetmobile.listeners.FragmentInteractionListener;

/**
 * A simple {@link Fragment} subclass
 * Use the {@link HelpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HelpFragment extends Fragment {
    private static final String TAG = "MapFragment";

    private static final boolean FAB_VISIBLE = false;
    private static final int TITLE_ID = R.string.help;

    private FragmentInteractionListener mListener;

    /** Required public constructor */
    public HelpFragment() { /* empty */ }

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_help, container, false);
        LinearLayout linearLayout = result.findViewById(R.id.helpLayout);
        for (String s : getResources().getStringArray(R.array.faq)) {
            TextView tv = new TextView(getActivity());
            tv.setText(Html.fromHtml(s));
            linearLayout.addView(tv);
        }

        /*
        // Define listener arguments
        if (mListener != null) {
            mListener.onFragmentInteraction(FAB_VISIBLE, TITLE_ID);
        }
        */

        return result;
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
