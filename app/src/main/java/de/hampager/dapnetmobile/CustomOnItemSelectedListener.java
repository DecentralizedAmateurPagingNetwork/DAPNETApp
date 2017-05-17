package de.hampager.dapnetmobile;

import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.AdapterView;

public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
    private final View rootView;

    public CustomOnItemSelectedListener(View root) {
        rootView = root;
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        TextInputLayout server = (TextInputLayout) rootView.findViewById(R.id.servertextinput);
        if (pos == 2) {
            server.setVisibility(View.VISIBLE);
        } else {
            server.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }
}

