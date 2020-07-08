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
        //probs should check if valid
        return new TransmitterGroup(completionText, "", new ArrayList<>(), new ArrayList<>());
    }

    @Override
    public void onFocusChanged(boolean hasFocus, int direction, Rect previous) {
        super.onFocusChanged(hasFocus, direction, previous);

        /* keep "ALL"
        if (hasFocus && getObjects().size() == 1 && getObjects().get(0).getName().equalsIgnoreCase("ALL")) {
            removeObject(getObjects().get(0));
        }
        */
    }

}
