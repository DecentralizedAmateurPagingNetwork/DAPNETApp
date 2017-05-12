package de.hampager.dapnetmobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import de.hampager.dapnetmobile.api.HamPagerService;
import de.hampager.dapnetmobile.api.HamnetCall;
import de.hampager.dapnetmobile.api.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostCallActivity extends AppCompatActivity {
    private static final String TAG = "PostCallActivity";
    private TextInputEditText message;
    private EditText callSignNames;
    private EditText transmitterGroupNames;
    private Boolean emergencyBool = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_call);
        message = (TextInputEditText) findViewById(R.id.post_call_text);
        callSignNames = (EditText) findViewById(R.id.post_call_callSignNames);
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

    private void sendCall() {
        String msg = message.getText().toString();
        List<String> csnl = Arrays.asList(callSignNames.getText().toString().split(" "));
        List<String> tgnl = Arrays.asList(transmitterGroupNames.getText().toString().split(" "));
        SharedPreferences sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
        String server = sharedPref.getString("server", null);
        String user = sharedPref.getString("user", null);
        String password = sharedPref.getString("pass", null);
        String[] returnString = {"example.com", "exampleUser", "examplePass"};

        if (msg.length() != 0 && msg.length() <= 80 && callSignNames.getText().toString().length() != 0)
            sendCallMethod(msg, csnl, tgnl, emergencyBool, server, user, password);
        else if (msg.length() == 0)
            genericSnackbar(getString(R.string.error_empty_msg));
        else if (msg.length() > 79)
            genericSnackbar(getString(R.string.error_msg_too_long));
        else if (callSignNames.getText().toString().length() == 0)
            genericSnackbar(getString(R.string.error_empty_callsignlist));
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
}
