package de.hampager.dapnetmobile.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import de.hampager.dapnetmobile.MainActivity;
import de.hampager.dapnetmobile.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WelcomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WelcomeFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String TAG = "WelcomeFragment";
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_welcome, container, false);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            if (bundle.getBoolean(ARG_PARAM1, false)){
                TextView mLogInText = (TextView) v.findViewById(R.id.call_call_sign);
                mLogInText.setText(R.string.welcome_loggedInText);
            }
        }
        TextView mLinkView1 = (TextView) v.findViewById(R.id.linkView1);
        mLinkView1.setMovementMethod(LinkMovementMethod.getInstance());
        TextView mLinkView2= (TextView) v.findViewById(R.id.linkView2);
        mLinkView2.setMovementMethod(LinkMovementMethod.getInstance());
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
