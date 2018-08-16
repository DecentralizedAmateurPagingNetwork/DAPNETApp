package de.hampager.dapnetmobile.tokenautocomplete;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tokenautocomplete.TokenCompleteTextView;

import de.hampager.dap4j.models.CallSign;
import de.hampager.dapnetmobile.R;


public class CallsignsCompletionView extends TokenCompleteTextView<CallSign> {

    public CallsignsCompletionView(Context context) {
        super(context);
    }

    public CallsignsCompletionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CallsignsCompletionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected View getViewForObject(CallSign callsign) {
        LayoutInflater l = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        TokenTextView token = (TokenTextView) l.inflate(R.layout.callsign_token, (ViewGroup) getParent(), false);
        token.setText(callsign.getName());
        return token;
    }

    @Override
    protected CallSign defaultObject(String completionText) {
        //If
        return null;
    }
}
