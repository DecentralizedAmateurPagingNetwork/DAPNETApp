package de.hampager.dapnetmobile;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.tokenautocomplete.FilteredArrayAdapter;
import com.tokenautocomplete.TokenCompleteTextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hampager.dapnetmobile.api.CallSignResource;
import de.hampager.dapnetmobile.api.HamPagerService;
import de.hampager.dapnetmobile.api.HamnetCall;
import de.hampager.dapnetmobile.api.ServiceGenerator;
import de.hampager.dapnetmobile.tokenautocomplete.CallsignsCompletionView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostCallActivity extends AppCompatActivity implements TokenCompleteTextView.TokenListener<CallSignResource> {
    private static final String TAG = "PostCallActivity";
    CallsignsCompletionView callSignsCompletion;
    String server;
    String user;
    String password;
    private TextInputEditText message;
    private EditText transmitterGroupNames;
    private Boolean emergencyBool = false;
    private List<String> csnl = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_call);
        defineObjects();
        SharedPreferences sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
        server = sharedPref.getString("server", "http://www.hampager.de:8080");
        user = sharedPref.getString("user", "invalid");
        password = sharedPref.getString("pass", "invalid");
        getCallsigns();
    }

    private void defineObjects() {
        message = (TextInputEditText) findViewById(R.id.post_call_text);
        transmitterGroupNames = (EditText) findViewById(R.id.post_call_transmitterGroupNames);
        Switch emergency = (Switch) findViewById(R.id.post_call_emergencyswitch);
        message.requestFocus();

        emergency.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                emergencyBool = isChecked;
            }
        });
        //TODO: On return pressed on keyboard->Send
        transmitterGroupNames.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    sendCall();
                    return true;
                }
                return false;
            }
        });
    }

    private void getCallsigns() {
        try {
            ServiceGenerator.changeApiBaseUrl(server);
        } catch (java.lang.NullPointerException e) {
            ServiceGenerator.changeApiBaseUrl("http://www.hampager.de:8080");
        }
        HamPagerService service = ServiceGenerator.createService(HamPagerService.class, user, password);
        Call<ArrayList<CallSignResource>> call;
        call = service.getAllCallSigns("");
        call.enqueue(new Callback<ArrayList<CallSignResource>>() {
            @Override
            public void onResponse(Call<ArrayList<CallSignResource>> call, Response<ArrayList<CallSignResource>> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "Connection was successful");
                    // tasks available
                    ArrayList<CallSignResource> data = response.body();
                    setCallsigns(data);
                    //adapter = new DataAdapter(data);
                } else {
                    //APIError error = ErrorUtils.parseError(response);
                    Log.e(TAG, "Error " + response.code());
                    Log.e(TAG, response.message());
                    if (response.code() == 401) {
                        SharedPreferences sharedPref = PostCallActivity.this.getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.clear();
                        editor.apply();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<CallSignResource>> call, Throwable t) {
                // something went completely wrong (e.g. no internet connection)
                Log.e(TAG, t.getMessage());
            }
        });
    }

    private void setCallsigns(ArrayList<CallSignResource> data) {
        CallSignResource[] cs = data.toArray(new CallSignResource[data.size()]);
        callSignsCompletion = (CallsignsCompletionView) findViewById(R.id.callSignSearchView);
        callSignsCompletion.setAdapter(generateAdapter(cs));
        callSignsCompletion.setTokenListener(this);
        callSignsCompletion.setTokenClickStyle(TokenCompleteTextView.TokenClickStyle.Select);
        callSignsCompletion.allowDuplicates(false);
    }

    private FilteredArrayAdapter<CallSignResource> generateAdapter(CallSignResource[] callsigns) {
        return new FilteredArrayAdapter<CallSignResource>(this, R.layout.callsign_layout, callsigns) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {

                    LayoutInflater l = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                    convertView = l.inflate(R.layout.callsign_layout, parent, false);
                }

                CallSignResource p = getItem(position);
                ((TextView) convertView.findViewById(R.id.name)).setText(p.getName());
                //((TextView)convertView.findViewById(R.id.email)).setText(p.getEmail());

                return convertView;
            }

            @Override
            protected boolean keepObject(CallSignResource callsign, String mask) {
                mask = mask.toLowerCase();
                //return callsign.getName().toLowerCase().startsWith(mask) || callsign.getEmail().toLowerCase().startsWith(mask);
                return callsign.getName().toLowerCase().startsWith(mask);
            }
        };
    }
    private void sendCall() {
        String msg = message.getText().toString();
        List<String> tgnl = Arrays.asList(transmitterGroupNames.getText().toString().split(" "));

        if (msg.length() != 0 && msg.length() <= 80 && callSignsCompletion.getText().toString().length() != 0) {
            Log.i(TAG, "CSNL,sendcall" + csnl.toString());
            sendCallMethod(msg, csnl, tgnl, emergencyBool, server, user, password);
        } else if (msg.length() == 0)
            genericSnackbar(getString(R.string.error_empty_msg));
        else if (msg.length() > 79)
            genericSnackbar(getString(R.string.error_msg_too_long));
        else if (callSignsCompletion.getText().toString().length() == 0)
            genericSnackbar(getString(R.string.error_empty_callsignlist));
    }

    private void genericSnackbar(String s) {
        Snackbar.make(findViewById(R.id.postcallactivityid), s, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    private void sendCallMethod(String msg, List<String> csnl, List<String> tgnl, boolean e, String server, String user, String password) {
        HamnetCall sendvalue = new HamnetCall(msg, csnl, tgnl, e);
        Log.i(TAG, csnl.toString());

        try {
            ServiceGenerator.changeApiBaseUrl(server);
        } catch (NullPointerException err) {
            ServiceGenerator.changeApiBaseUrl("http://www.hampager.de:8080");
        }
        HamPagerService service = ServiceGenerator.createService(HamPagerService.class, user, password);
        Call<HamnetCall> call = service.postHamnetCall(sendvalue);
        call.enqueue(new Callback<HamnetCall>() {
            @Override
            public void onResponse(Call<HamnetCall> call, Response<HamnetCall> response) {
                if (response.isSuccessful()) {
                    // tasks available
                    //HamnetCall returnValue = response.body();
                    Toast.makeText(PostCallActivity.this, getString(R.string.successfully_sent_message), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    //APIError error = ErrorUtils.parseError(response);
                    Log.e(TAG, "Post Call Error: " + response.code());
                    genericSnackbar("Error:" + response.code() + "Msg: " + response.message());
                    if (response.code() == 401) {
                        SharedPreferences sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.clear();
                        editor.apply();
                    }
                }
            }

            @Override
            public void onFailure(Call<HamnetCall> call, Throwable t) {
                // something went completely south (like no internet connection)
                Log.e(TAG, t.toString());
                Snackbar.make(getWindow().getDecorView().getRootView(), getString(R.string.error_no_internet), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sendbutton, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_send) {
            // Check if no view has focus:
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            sendCall();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTokenAdded(CallSignResource token) {
        csnl.add(token.getName());
    }

    @Override
    public void onTokenRemoved(CallSignResource token) {
        csnl.remove(token.getName());
    }

}
