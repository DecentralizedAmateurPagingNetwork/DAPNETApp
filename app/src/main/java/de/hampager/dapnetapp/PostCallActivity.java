package de.hampager.dapnetapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import de.hampager.dapnetapp.api.HamPagerService;
import de.hampager.dapnetapp.api.HamnetCall;
import de.hampager.dapnetapp.api.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostCallActivity extends AppCompatActivity {
    private static final String TAG = "PostCallActivity";
    private static final String jsonData = "saveData";
    private EditText message;
    private EditText callSignNames;
    private EditText transmitterGroupNames;
    private Boolean emergencyBool = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_call);
        message = (EditText) findViewById(R.id.post_call_text);
        callSignNames = (EditText) findViewById(R.id.post_call_callSignNames);
        transmitterGroupNames = (EditText) findViewById(R.id.post_call_transmitterGroupNames);
        Switch emergency = (Switch) findViewById(R.id.post_call_emergencyswitch);
        emergency.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                emergencyBool = isChecked;
            }
        });
        //TODO: Fix on return press->Send
        transmitterGroupNames.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    sendCall();
                    return true;
                }
                return false;
            }
        });
    }

    //TODO: Get rid of defaultview

    private void sendCall() {
        String msg = message.getText().toString();
        List<String> csnl = Arrays.asList(callSignNames.getText().toString().split(" "));
        List<String> tgnl = Arrays.asList(transmitterGroupNames.getText().toString().split(" "));
        SharedPreferences sharedPref = getSharedPreferences("sharedPref",Context.MODE_PRIVATE);

        String strJson = sharedPref.getString(jsonData, "0");//second parameter is necessary ie.,Value to return if this preference does not exist.
        JSONObject jobj = null;
        try {
            jobj = new JSONObject(strJson);
        } catch (org.json.JSONException e) {
            Log.e(TAG, "Error reading JSON Object");
        }
            String[] returnString = {"example.com", "exampleUser", "examplePass"};
            try {
                returnString[0] = jobj.get("server").toString();
                returnString[1] = jobj.get("user").toString();
                returnString[2] = jobj.get("pass").toString();
            } catch (org.json.JSONException e) {
                Log.e(TAG, "Error reading JSON Object, have you logged in?");
            }

        if (msg.length() != 0 && msg.length() <= 80 && callSignNames.getText().toString().length() != 0)
            sendCallMethod(msg, csnl, tgnl, emergencyBool, returnString[0], returnString[1], returnString[2]);
        else if (msg.length() == 0)
            genericSnackbar("Error: Message empty!");
        else if (msg.length() > 79)
            genericSnackbar("Error: Message above 80 Characters");
        else if (callSignNames.getText().toString().length() == 0)
            genericSnackbar("Error: Callsignlist is empty");
    }

    private void genericSnackbar(String s) {
        Snackbar.make(findViewById(R.id.postcallactivityid), s, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    private void sendCallMethod(String msg, List<String> csnl, List<String> tgnl, boolean e, String server, String user, String password) {
        HamnetCall sendvalue = new HamnetCall(msg, csnl, tgnl, e);
        ServiceGenerator.changeApiBaseUrl(server);
        HamPagerService service = ServiceGenerator.createService(HamPagerService.class, user, password);
        Call<HamnetCall> call = service.postHamnetCall(sendvalue);
        call.enqueue(new Callback<HamnetCall>() {
            @Override
            public void onResponse(Call<HamnetCall> call, Response<HamnetCall> response) {
                if (response.isSuccessful()) {
                    // tasks available
                    //HamnetCall returnValue = response.body();

                    genericSnackbar("Successfully sent message");
                } else {
                    //APIError error = ErrorUtils.parseError(response);
                    Log.e(TAG,"Post Call Error: "+response.code());
                    genericSnackbar("Error:" + response.code() + "MSG: " + response.message());
                    if (response.code()==401){
                        SharedPreferences sharedPref = getSharedPreferences("sharedPref",Context.MODE_PRIVATE);
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
                Snackbar.make(getWindow().getDecorView().getRootView(), "Error. Do you have Internet Access?", Snackbar.LENGTH_LONG).setAction("Action", null).show();
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
    //TODO: Add error messages
    //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
}
