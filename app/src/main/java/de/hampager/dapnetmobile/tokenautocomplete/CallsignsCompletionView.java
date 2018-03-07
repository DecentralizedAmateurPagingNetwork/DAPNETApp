package de.hampager.dapnetmobile.tokenautocomplete;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tokenautocomplete.TokenCompleteTextView;

import de.hampager.dapnetmobile.R;
import de.hampager.dap4j.models.CallSignResource;


public class CallsignsCompletionView extends TokenCompleteTextView<CallSignResource> {

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
    protected View getViewForObject(CallSignResource callsign) {
        LayoutInflater l = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        TokenTextView token = (TokenTextView) l.inflate(R.layout.callsign_token, (ViewGroup) getParent(), false);
        token.setText(callsign.getName());
        return token;
    }

    @Override
    protected CallSignResource defaultObject(String completionText) {
        //If
        return null;
    }
}
