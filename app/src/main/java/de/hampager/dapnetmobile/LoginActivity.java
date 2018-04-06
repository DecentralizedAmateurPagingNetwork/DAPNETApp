package de.hampager.dapnetmobile;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import de.hampager.dap4j.DAPNET;
import de.hampager.dap4j.DapnetSingleton;
import de.hampager.dap4j.callbacks.DapnetListener;
import de.hampager.dap4j.callbacks.DapnetResponse;
import de.hampager.dap4j.models.User;



/**
 * A login screen that offers login via username/password.
 */

public class LoginActivity extends AppCompatActivity {
    private final String TAG = "LoginActivity";

    // UI references.
    private TextInputEditText mServerView;
    private TextInputEditText mUsernameView;
    private TextInputEditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Spinner spinner;
    private Button mSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mServerView = (TextInputEditText) findViewById(R.id.server);
        mUsernameView = (TextInputEditText) findViewById(R.id.user);
        mPasswordView = (TextInputEditText) findViewById(R.id.password);
        mSignInButton = (Button) findViewById(R.id.user_sign_in_button);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        addListenerOnButton();
        mUsernameView.requestFocus();
    }

    public void addListenerOnButton() {
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener(findViewById(R.id.loginactivityid)));

        String defServer = getIntent().getStringExtra("defServer");

        if (defServer != null && !(defServer.equals(getResources().getString(R.string.ClearNetURL)))) {
            spinner.setSelection(1);
        }
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                attemptLogin();
                return false;
            }
        });

        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    public User getUser(final String user, final String password, final String server) {
        DapnetSingleton dapnetSingleton = DapnetSingleton.getInstance();
        dapnetSingleton.init(getResources().getString(R.string.ClearNetURL), user, password);
        Log.d(TAG, dapnetSingleton.getUrl() + "; " + dapnetSingleton.getUser() + "; " + dapnetSingleton.getPass());
        DAPNET dapnet = dapnetSingleton.getDapnet();
        String name = user;
        User returnValue;
        dapnet.getUser(name, new DapnetListener<User>() {
            @Override
            public void onResponse(DapnetResponse<User> dapnetResponse) {

                if (dapnetResponse.isSuccessful()) {
                User returnValue = dapnetResponse.body();
                    saveData(server, user, password, true);
                    Log.i(TAG, "getUser, admin: " + true);
                    showProgress(false);
                    Log.i(TAG, "Login was successful!");
                    Toast.makeText(LoginActivity.this, getString(R.string.success_welcome) + user, Toast.LENGTH_SHORT).show();
                    Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                    LoginActivity.this.startActivity(myIntent);
                    finish();

                } else {

                    Log.e(TAG, "Error: ");
                    //TODO: implement .code
                    //Log.e(TAG, "Error: " + dapnetResponse().code);
                    //Should Use APIError

                    //Log.e(TAG, error.message());
                    showProgress(false);
                    View focusView = mUsernameView;
                    focusView.requestFocus();
                    Snackbar.make(findViewById(R.id.loginactivityid), getString(R.string.error_credentials), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e(TAG, "Call failed. Do you have Internet access?");
                showProgress(false);
                View focusView = mUsernameView;
                focusView.requestFocus();
                Snackbar.make(findViewById(R.id.loginactivityid), getString(R.string.error_no_internet), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
        return null;
    }

    private void attemptLogin() {

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);
        String server = mServerView.getText().toString();
        boolean cancel = false;
        View focusView = null;
        // Store values at the time of the login attempt
        if (spinner.getSelectedItemPosition() == 2) {
            // Check for a valid server address.
            if (TextUtils.isEmpty(server)) {
                mServerView.setError(getString(R.string.error_field_required));
                focusView = mServerView;
                cancel = true;
            } else if (!isServerValid(server)) {
                mServerView.setError(getString(R.string.error_invalid_server));
                focusView = mServerView;
                cancel = true;
            }
        } else if (spinner.getSelectedItemPosition() == 1) {
            server = getResources().getString(R.string.DapNetURL);
        } else {
            server = getResources().getString(R.string.ClearNetURL);
        }
        String user = mUsernameView.getText().toString().trim();
        String password = mPasswordView.getText().toString();


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
            mUsernameView.setError(getString(R.string.error_invalid_user));
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

    private boolean isServerValid(String server) {
        return Patterns.WEB_URL.matcher(server).matches();
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

