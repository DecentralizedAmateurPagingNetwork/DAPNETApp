package de.hampager.dapnetmobile;

import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
    private final View rootView;

    public CustomOnItemSelectedListener(View root) {
        rootView = root;
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        EditText server = (EditText) rootView.findViewById(R.id.server);
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

