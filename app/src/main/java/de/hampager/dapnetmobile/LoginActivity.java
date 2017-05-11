package de.hampager.dapnetmobile;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import de.hampager.dapnetmobile.api.HamPagerService;
import de.hampager.dapnetmobile.api.ServiceGenerator;
import de.hampager.dapnetmobile.api.UserResource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A login screen that offers login via username/password.
 */

public class LoginActivity extends AppCompatActivity {
    private static final String jsonData = "saveData";
    private final String TAG = "LoginActivity";

    // UI references.
    private EditText mServerView;
    private EditText mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //setupActionBar();
        // Set up the login form.
        mServerView = (EditText) findViewById(R.id.server);
        mUsernameView = (EditText) findViewById(R.id.user);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.user_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        ImageButton mSettingsButton = (ImageButton) findViewById(R.id.settingsImageButton);
        mSettingsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //TextInputLayout til =(TextInputLayout)findViewById(R.id.servertextinput);
                if (mServerView.getVisibility() == View.VISIBLE) {
                    mServerView.setVisibility(View.GONE);
                } else {
                    mServerView.setVisibility(View.VISIBLE);
                }
            }
        });
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

    }

    public UserResource getUser(final String user, final String password, final String server) {
        UserResource returnValue = null;
        ServiceGenerator.changeApiBaseUrl(server);
        HamPagerService service = ServiceGenerator.createService(HamPagerService.class, user, password);

        Call<UserResource> call = service.getUserResource(user);
        call.enqueue(new Callback<UserResource>() {
            @Override
            public void onResponse(Call<UserResource> UserResource, Response<UserResource> response) {
                if (response.isSuccessful()) {
                    UserResource returnValue = response.body();
                    saveData(server, user, password);
                    Log.i(TAG,"getUser, admin: "+returnValue.admin());
                    saveAdmin(returnValue.admin());
                    showProgress(false);
                    Log.i(TAG, "Login was successful!");
                    Snackbar.make(findViewById(R.id.loginactivityid), "Success! Welcome " + user, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                    //TODO: Add extra?
                    //myIntent.putExtra("key", value); //Optional parameters
                    LoginActivity.this.startActivity(myIntent);
                    finish();
                } else {
                    Log.e(TAG, "Error: " + response.code());
                    //TODO: User APIError
                    //APIError error = ErrorUtils.parseError(response);
                    //Log.e(TAG, error.message());
                    showProgress(false);
                    View focusView = mUsernameView;
                    focusView.requestFocus();
                    Snackbar.make(findViewById(R.id.loginactivityid), "Error. Are your credentials correct?", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }

            @Override
            public void onFailure(Call<UserResource> userRessource, Throwable t) {
                Log.e(TAG, "Call failed. Do you have Internet access?");
                showProgress(false);
                View focusView = mUsernameView;
                focusView.requestFocus();
                Snackbar.make(findViewById(R.id.loginactivityid), "Error. Do you have Internet access?", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        return returnValue;
    }

    private void attemptLogin() {


        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String server = mServerView.getText().toString();
        String user = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid user address.
        if (TextUtils.isEmpty(user)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!isEmailValid(user)) {
            mUsernameView.setError(getString(R.string.error_invalid_email));
            focusView = mUsernameView;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            Log.i(TAG, "Logging in...");
            saveData(server, user, password);
            getUser(user, password,server);
        }
    }

    //TODO: Replace this with your own logic
    private boolean isEmailValid(String user) {
        return true;
    }

    private boolean isPasswordValid(String password) {
        return true;
    }


    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        // MinSDK is 15
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });

    }

    //TODO: Add error messages
    public void saveAdmin(boolean admin) {
        Log.i(TAG,"saveAdmin: admin: "+admin);
        SharedPreferences sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putBoolean("admin", admin);
        editor.apply();
    }

    public void saveData(String server, String user, String pass) {
        SharedPreferences sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        JSONObject jobj = new JSONObject();
        try {
            jobj.put("server", server);
            jobj.put("user", user);
            jobj.put("pass", pass);
        } catch (JSONException e) {
            Log.e(TAG, "Error writing JSON object");
        }
        editor.putString(jsonData, jobj.toString());
        editor.apply();
        Log.i(TAG, "Saved credentials.");
    }

    public JSONObject loadJSONData() {
        Log.i(TAG, "Loading JSON data");
        SharedPreferences sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
        String strJson = sharedPref.getString(jsonData, "0");//second parameter is necessary ie.,Value to return if this preference does not exist.
        JSONObject jobj = null;
        try {
            jobj = new JSONObject(strJson);
        } catch (JSONException e) {
            Log.e(TAG, "Error reading JSON Object");
        }
        return jobj;
    }

    public String[] loadStringData() {
        JSONObject jObject = loadJSONData();
        String[] returnString = {"example.com", "exampleUser", "examplePass"};
        try {
            returnString[0] = jObject.get("server").toString();
            returnString[1] = jObject.get("user").toString();
            returnString[2] = jObject.get("pass").toString();
        } catch (JSONException e) {
            Log.e(TAG, "Error reading JSON Object");
        }
        return returnString;
    }
}

