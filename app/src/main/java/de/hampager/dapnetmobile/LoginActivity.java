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
import android.widget.Toast;

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
                    saveData(server, user, password, returnValue.admin());
                    Log.i(TAG, "getUser, admin: " + returnValue.admin());
                    showProgress(false);
                    Log.i(TAG, "Login was successful!");
                    Toast.makeText(LoginActivity.this, getString(R.string.success_welcome) + user, Toast.LENGTH_SHORT).show();
                    Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                    LoginActivity.this.startActivity(myIntent);
                    finish();

                } else {
                    Log.e(TAG, "Error: " + response.code());
                    //TODO: Use APIError
                    //APIError error = ErrorUtils.parseError(response);
                    //Log.e(TAG, error.message());
                    showProgress(false);
                    View focusView = mUsernameView;
                    focusView.requestFocus();
                    Snackbar.make(findViewById(R.id.loginactivityid), getString(R.string.error_credentials), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }

            @Override
            public void onFailure(Call<UserResource> userRessource, Throwable t) {
                Log.e(TAG, "Call failed. Do you have Internet access?");
                showProgress(false);
                View focusView = mUsernameView;
                focusView.requestFocus();
                Snackbar.make(findViewById(R.id.loginactivityid), getString(R.string.error_no_internet), Snackbar.LENGTH_LONG).setAction("Action", null).show();
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
            getUser(user, password, server);
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

    public void saveData(String server, String user, String pass, Boolean admin) {
        SharedPreferences sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("server", server);
        editor.putString("user", user);
        editor.putString("pass", pass);
        editor.putBoolean("admin", admin);
        editor.putBoolean("isLoggedIn", true);
        editor.apply();
        Log.i(TAG, "Saved credentials.");
    }
}

