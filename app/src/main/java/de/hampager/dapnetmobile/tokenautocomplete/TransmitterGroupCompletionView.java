package de.hampager.dapnetmobile.tokenautocomplete;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tokenautocomplete.TokenCompleteTextView;

import java.util.ArrayList;

import de.hampager.dap4j.models.TransmitterGroup;
import de.hampager.dapnetmobile.R;

public class TransmitterGroupCompletionView extends TokenCompleteTextView<TransmitterGroup> {

    public TransmitterGroupCompletionView(Context context) {
        super(context);
    }

    public TransmitterGroupCompletionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TransmitterGroupCompletionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected View getViewForObject(TransmitterGroup transmittergroup) {
        LayoutInflater l = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        TokenTextView token = (TokenTextView) l.inflate(R.layout.callsign_token, (ViewGroup) getParent(), false);
        token.setText(transmittergroup.getName());
        return token;
    }

    @Override
    protected TransmitterGroup defaultObject(String completionText) {
        //Stupid simple example of guessing if we have an email or not
        /*int index = completionText.indexOf('@');
        if (index == -1) {*/
        return new TransmitterGroup(completionText, "", new ArrayList<>(), new ArrayList<>());
        /*} else {
            return new TransmitterGroup(completionText.substring(0, index),"");
        }*/
    }

    @Override
    public void onFocusChanged(boolean hasFocus, int direction, Rect previous) {
        super.onFocusChanged(hasFocus, direction, previous);
        if (hasFocus && getObjects().size() == 1 && getObjects().get(0).getName().toUpperCase().equals("ALL")) {
            removeObject(getObjects().get(0));
        }
    }

    private void displayList() {

    }
}
