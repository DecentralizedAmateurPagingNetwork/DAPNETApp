package de.hampager.dapnetmobile;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tokenautocomplete.FilteredArrayAdapter;
import com.tokenautocomplete.TokenCompleteTextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hampager.dapnetmobile.api.CallSignResource;
import de.hampager.dapnetmobile.api.HamPagerService;
import de.hampager.dapnetmobile.api.HamnetCall;
import de.hampager.dapnetmobile.api.ServiceGenerator;
import de.hampager.dapnetmobile.api.TransmitterGroupResource;
import de.hampager.dapnetmobile.tokenautocomplete.CallsignsCompletionView;
import de.hampager.dapnetmobile.tokenautocomplete.TransmitterGroupCompletionView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostCallActivity extends AppCompatActivity implements TokenCompleteTextView.TokenListener<CallSignResource> {
    private static final String TAG = "PostCallActivity";
    CallsignsCompletionView callSignsCompletion;
    TransmitterGroupCompletionView transmitterGroupCompletion;
    String server;
    String user;
    String password;
    private TextInputEditText message;
    //private EditText transmitterGroupNames
    private Boolean emergencyBool = false;
    private List<String> csnl = new ArrayList<>();
    private List<String> tgnl = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_call);
        SharedPreferences sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
        server = sharedPref.getString("server", "http://www.hampager.de:8080");
        user = sharedPref.getString("user", "invalid");
        password = sharedPref.getString("pass", "invalid");
        if (user.equals("invalid")&&password.equals("invalid")&&server.equals("http://www.hampager.de:8080"))
            Snackbar.make(findViewById(R.id.postcallactivityid), "You don't seem to be logged in.", Snackbar.LENGTH_LONG).show();
        Gson gson = new Gson();
        String callsignJson = sharedPref.getString("callsigns", "");
        CallSignResource[] callSignResources=gson.fromJson(callsignJson,CallSignResource[].class);
        if (callSignResources!=null){
            setCallsigns(callSignResources);
        }
        String transmitterJson = sharedPref.getString("transmitters","");
        TransmitterGroupResource[] transmitterGroupResources= gson.fromJson(transmitterJson,TransmitterGroupResource[].class);
        if (transmitterGroupResources!=null)
        setTransmittergroups(transmitterGroupResources);
        defineObjects();
        getCallsigns();
        getTransmitterGroups();
    }

    private void defineObjects() {
        Log.i("TEST", "TEST");
        message = (TextInputEditText) findViewById(R.id.post_call_text);
        Switch emergency = (Switch) findViewById(R.id.post_call_emergencyswitch);
        String m = user.toUpperCase();
        m += ": ";
        message.setText(m);
        message.requestFocus();
        emergency.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                emergencyBool = isChecked;
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
                    Log.i(TAG, "Connection getting Callsigns was successful");
                    // tasks available
                    ArrayList<CallSignResource> data = response.body();
                    Collections.sort(data, new Comparator<CallSignResource>() {
                        @Override
                        public int compare(CallSignResource o1, CallSignResource o2) {
                            int n = o1.getName().compareTo(o2.getName());
                            if (n==0)
                                return o1.getDescription().compareTo(o2.getDescription());
                            else
                                return n;
                        }
                    });
                    CallSignResource[] dataArray = data.toArray(new CallSignResource[data.size()]);
                    saveData(dataArray);
                    setCallsigns(dataArray);
                    //adapter = new CallAdapter(data);
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

    private void setCallsigns(CallSignResource[] data) {
        callSignsCompletion = (CallsignsCompletionView) findViewById(R.id.callSignSearchView);
        callSignsCompletion.setAdapter(generateAdapter(data));
        callSignsCompletion.setTokenListener(this);
        callSignsCompletion.setTokenClickStyle(TokenCompleteTextView.TokenClickStyle.Select);
        callSignsCompletion.allowDuplicates(false);
        callSignsCompletion.setThreshold(0);
        callSignsCompletion.performBestGuess(true);
    }
    private void saveData(CallSignResource[] input){
        SharedPreferences sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(input);
        prefsEditor.putString("callsigns", json);
        prefsEditor.apply();
    }
    private void saveData(TransmitterGroupResource[] input){
        SharedPreferences sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(input);
        prefsEditor.putString("transmitters", json);
        prefsEditor.apply();
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
                ((TextView) convertView.findViewById(R.id.token_desc)).setText(p.getDescription());

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


    private void getTransmitterGroups() {
        try {
            ServiceGenerator.changeApiBaseUrl(server);
        } catch (java.lang.NullPointerException e) {
            ServiceGenerator.changeApiBaseUrl("http://www.hampager.de:8080");
        }
        HamPagerService service = ServiceGenerator.createService(HamPagerService.class, user, password);
        Call<ArrayList<TransmitterGroupResource>> call;
        call = service.getAllTransmitterGroups();
        call.enqueue(new Callback<ArrayList<TransmitterGroupResource>>() {
            @Override
            public void onResponse(Call<ArrayList<TransmitterGroupResource>> call, Response<ArrayList<TransmitterGroupResource>> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "Connection getting transmittergroups was successful");
                    // tasks available
                    ArrayList<TransmitterGroupResource> data = response.body();
                    Collections.sort(data, new Comparator<TransmitterGroupResource>() {
                        @Override
                        public int compare(TransmitterGroupResource o1, TransmitterGroupResource o2) {
                            int n = o1.getName().compareTo(o2.getName());
                            if (n==0)
                                return o1.getDescription().compareTo(o2.getDescription());
                            else
                                return n;
                        }
                    });
                    TransmitterGroupResource[] transmitterGroupResources =data.toArray(new TransmitterGroupResource[data.size()]);
                    saveData(transmitterGroupResources);
                    setTransmittergroups(transmitterGroupResources);
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
            public void onFailure(Call<ArrayList<TransmitterGroupResource>> call, Throwable t) {
                // something went completely wrong (e.g. no internet connection)
                Log.e(TAG, t.getMessage());
            }
        });
    }

    private void setTransmittergroups(TransmitterGroupResource[] data) {
        transmitterGroupCompletion = (TransmitterGroupCompletionView) findViewById(R.id.transmittergroupSearchView);
        transmitterGroupCompletion.setAdapter(generateAdapter(data));
        transmitterGroupCompletion.setTokenListener(new tokenTransmitter());
        transmitterGroupCompletion.setTokenClickStyle(TokenCompleteTextView.TokenClickStyle.Select);
        transmitterGroupCompletion.allowDuplicates(false);
        transmitterGroupCompletion.performBestGuess(true);
        transmitterGroupCompletion.setThreshold(1);
        if (transmitterGroupCompletion.getObjects()==null||transmitterGroupCompletion.getObjects().size()==0){
            transmitterGroupCompletion.addObject(new TransmitterGroupResource("ALL"));
        }
    }

    private FilteredArrayAdapter<TransmitterGroupResource> generateAdapter(TransmitterGroupResource[] transmittergroups) {
        return new FilteredArrayAdapter<TransmitterGroupResource>(this, R.layout.callsign_layout, transmittergroups) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {

                    LayoutInflater l = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                    convertView = l.inflate(R.layout.callsign_layout, parent, false);
                }

                TransmitterGroupResource p = getItem(position);
                ((TextView) convertView.findViewById(R.id.name)).setText(p.getName());
                ((TextView) convertView.findViewById(R.id.token_desc)).setText(p.getDescription());

                return convertView;
            }

            @Override
            protected boolean keepObject(TransmitterGroupResource transmittergroup, String mask) {
                mask = mask.toLowerCase();
                //return callsign.getName().toLowerCase().startsWith(mask) || callsign.getEmail().toLowerCase().startsWith(mask);
                return transmittergroup.getName().toLowerCase().startsWith(mask);
            }
        };
    }

    private void sendCall() {
        String msg = message.getText().toString();
        if (callSignsCompletion != null) {
            if (msg.length() != 0 && msg.length() <= 80 && callSignsCompletion.getText().toString().length() != 0) {
                Log.i(TAG, "CSNL,sendcall" + csnl.toString());
                //Forcing completion by focus change to prevent Issue #45
                callSignsCompletion.onFocusChanged(false,View.FOCUS_FORWARD,null);
                transmitterGroupCompletion.onFocusChanged(false,View.FOCUS_FORWARD,null);
                sendCallMethod(msg, csnl, tgnl, emergencyBool, server, user, password);
            } else if (msg.length() == 0)
                genericSnackbar(getString(R.string.error_empty_msg));
            else if (msg.length() > 79)
                genericSnackbar(getString(R.string.error_msg_too_long));
            else if (callSignsCompletion.getText().toString().length() == 0)
                genericSnackbar(getString(R.string.error_empty_callsignlist));
        } else {
            genericSnackbar(getString(R.string.generic_error));
        }
    }

    private void genericSnackbar(String s) {
        Snackbar.make(findViewById(R.id.postcallactivityid), s, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    private void sendCallMethod(String msg, List<String> csnl, List<String> tgnl, boolean e, String server, String user, String password) {
        HamnetCall sendvalue = new HamnetCall(msg, csnl, tgnl, e);
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
                    Log.i(TAG, "Sending call worked with successful response");
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
                Log.e(TAG, "Sending call seems to have failed");
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

    public class tokenTransmitter implements TokenCompleteTextView.TokenListener<TransmitterGroupResource> {
        @Override
        public void onTokenAdded(TransmitterGroupResource token) {
            tgnl.add(token.getName());
        }

        @Override
        public void onTokenRemoved(TransmitterGroupResource token) {
            tgnl.remove(token.getName());
        }
    }

}