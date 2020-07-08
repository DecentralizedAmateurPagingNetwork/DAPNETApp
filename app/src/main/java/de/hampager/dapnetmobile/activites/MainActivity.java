package de.hampager.dapnetmobile.activites;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Objects;

import de.hampager.dap4j.DAPNET;
import de.hampager.dap4j.DapnetSingleton;
import de.hampager.dap4j.callbacks.DapnetListener;
import de.hampager.dap4j.callbacks.DapnetResponse;
import de.hampager.dap4j.models.Version;
import de.hampager.dapnetmobile.BuildConfig;
import de.hampager.dapnetmobile.R;
import de.hampager.dapnetmobile.fragments.CallFragment;
import de.hampager.dapnetmobile.fragments.HelpFragment;
import de.hampager.dapnetmobile.fragments.MapFragment;
import de.hampager.dapnetmobile.fragments.PrivacyFragment;
import de.hampager.dapnetmobile.fragments.TableFragment;
import de.hampager.dapnetmobile.fragments.WelcomeFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    public static final String SP = "sharedPref";

    private Toolbar toolbar;
    // private FloatingActionButton fab;
    private Button fab; // "New" fab
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private FrameLayout frameLayout;
    private TextView mNavHeadVersions;

    boolean loggedIn = false;
    private String mServer;
    private MenuItem mPreviousMenuItem;
    private boolean isDrawerLocked = false;

    private MenuItem loginMenuItem, loginStatusMenuItem;

    private WelcomeFragment welcomeFragment;
    private boolean isMapFull = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.i(TAG, "method: onCreate");

        // "FloatingActionButton"
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            if (loggedIn) {
                Intent myIntent = new Intent(MainActivity.this, PostCallActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
            else {
                genericSnackbar(getString(R.string.error_logged_in));
            }
        });

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            // Launch WelcomeFragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.container, WelcomeFragment.newInstance(loggedIn), "WelcomeFragment");
            ft.commit();
        }

        frameLayout = findViewById(R.id.content_frame);
        if (((ViewGroup.MarginLayoutParams) frameLayout.getLayoutParams()).leftMargin == (int) getResources().getDimension(R.dimen.drawer_size)) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
            drawer.setScrimColor(Color.TRANSPARENT);
            isDrawerLocked = true;
            Log.d(TAG, "Drawer locked");
        }
        if (!isDrawerLocked) {
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                    drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
        }
        setVersion();

        // Obtain login status
        if (savedInstanceState == null) {
            boolean loggedIn = getSharedPreferences(SP, Context.MODE_PRIVATE).getBoolean("isLoggedIn", false);
            // User is not logged in- start LoginActivity
            if (!loggedIn) {
                startActivity(new Intent(this, LoginActivity.class));
            }
        }
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "method: onBackPressed");

        drawer = findViewById(R.id.drawer_layout); // already created in onCreate?
        if (drawer.isDrawerOpen(GravityCompat.START) && isDrawerLocked) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            SharedPreferences sharedPref = getSharedPreferences(SP, Context.MODE_PRIVATE);
            loggedIn = sharedPref.getBoolean("isLoggedIn", false);
            if (isMapFull) {
                isMapFull = welcomeFragment.setMapFull(false);
            }
            else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.i(TAG, "method: onPrepareOptionsMenu");

        navigationView = findViewById(R.id.nav_view);
        Menu nv = navigationView.getMenu();
        loginStatusMenuItem = nv.findItem(R.id.nav_loginstatus);
        SharedPreferences sharedPref = getSharedPreferences(SP, Context.MODE_PRIVATE);
        loggedIn = sharedPref.getBoolean("isLoggedIn", false);

        setLoginMenuItems(loggedIn);
        return true;
    }

    /**
     * Updates "Log in"/"Log out" menu item on left navigation drawer and
     * shows/hides "Log in" menu item on top navigation bar.
     *
     * @param loggedIn  Flag set when user logs in/out
     */
    private void setLoginMenuItems(boolean loggedIn) {
        Log.i(TAG, (loggedIn) ? "User is logged in!" : "User is not logged in!");
        loginStatusMenuItem.setTitle(((loggedIn) ? R.string.nav_logout : R.string.nav_login));
        loginMenuItem.setVisible(!loggedIn);
    }

    @Override
    public void onResume() {
        Log.i(TAG, "method: onResume");
        // temp
        welcomeFragment =  (WelcomeFragment) getSupportFragmentManager().findFragmentByTag("WelcomeFragment");
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "method: onCreateOptionsMenu");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        loginMenuItem = menu.findItem(R.id.action_login);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "method: onOptionsItemSelected");

        switch (item.getItemId()) {
            case R.id.action_login:
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Handles action bar item clicks. The action bar will automatically handle clicks on the home/up button, so long
     * as you specify a parent activity in AndroidManifest.xml.
     *
     * @param item  An item on the navigation drawer
     * @return true
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.i(TAG, "method: onNavigationItemSelected");

        if (isMapFull) {
            isMapFull = welcomeFragment.setMapFull(false);
        }

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        // item.setCheckable(true)
        item.setChecked(true);
        if (mPreviousMenuItem != null && !(mPreviousMenuItem.equals(item))) {
            mPreviousMenuItem.setChecked(false);
        }
        mPreviousMenuItem = item;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        switch (id) {
            case R.id.nav_home:
                // todo: define behavior
                break;
            case R.id.nav_calls:
                if (loggedIn) {
                    ft.replace(R.id.container, new CallFragment()).addToBackStack("CALLS").commit();
                    //ft.replace(R.id.container, new CallFragment()).commit();
                }
                else {
                    genericSnackbar(getString(R.string.error_logged_in));
                }
                break;
            case R.id.nav_subscribers:
                if (loggedIn) {
                    ft.replace(R.id.container, TableFragment.newInstance(TableFragment.TableTypes.SUBSCRIBERS))
                            .addToBackStack("SUBSCRIBERS").commit();
                    //ft.replace(R.id.container, TableFragment.newInstance(TableFragment.TableTypes.SUBSCRIBERS)).commit();
                }
                else {
                    genericSnackbar(getString(R.string.error_logged_in));
                }
                break;
            case R.id.nav_map:
                // setActionBarTitle("DAPNET map");
                // ft.replace(R.id.container, new MapFragment()).addToBackStack("MAP").commit();
                isMapFull = welcomeFragment.setMapFull(true);
                break;
            case R.id.nav_transmitters:
                ft.replace(R.id.container, TableFragment.newInstance(TableFragment.TableTypes.TRANSMITTERS))
                        .addToBackStack("TRANSMITTERS").commit();
                break;
            case R.id.nav_transmitterGroups:
                ft.replace(R.id.container, TableFragment.newInstance(TableFragment.TableTypes.TRANSMITTER_GROUPS))
                        .addToBackStack("TRANSMITTER_GROUPS").commit();
                break;
            case R.id.nav_rubrics:
                if (loggedIn) {
                    ft.replace(R.id.container, TableFragment.newInstance(TableFragment.TableTypes.RUBRICS))
                            .addToBackStack("RUBRICS").commit();
                }
                else {
                    genericSnackbar(getString(R.string.error_logged_in));
                }
                break;
            case R.id.nav_nodes:
                if (loggedIn) {
                    ft.replace(R.id.container, TableFragment.newInstance(TableFragment.TableTypes.NODES))
                            .addToBackStack("NODES").commit();
                }
                else {
                    genericSnackbar(getString(R.string.error_logged_in));
                }
                break;
            case R.id.nav_users:
                if (loggedIn) {
                    ft.replace(R.id.container, TableFragment.newInstance(TableFragment.TableTypes.USERS))
                            .addToBackStack("USERS").commit();
                }
                else {
                    genericSnackbar(getString(R.string.error_logged_in));
                }
                break;
            case R.id.nav_loginstatus:
                SharedPreferences pref = getSharedPreferences(SP, Context.MODE_PRIVATE);
                if (loggedIn) {
                    // Clear preferences
                    SharedPreferences.Editor editor = pref.edit();
                    editor.clear();
                    editor.putBoolean("privacy_activity_executed", true); // user has already seen PrivacyActivity upon 1st launch
                    editor.putBoolean("isLoggedIn", false); // user is logging off
                    loggedIn = false; // update flag
                    editor.apply();
                }
                setLoginMenuItems(loggedIn); // update menu items (nav. drawer item to state "Log in" and show nav. bar item)
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.putExtra("defServer", mServer);
                startActivity(intent);
                break;
            case R.id.nav_help:
                setActionBarTitle("DAPNET help");
                ft.replace(R.id.container, new HelpFragment()).addToBackStack("HELP").commit();
                break;
            case R.id.nav_feedbacklink:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.github_feedback)));
                startActivity(browserIntent);
                break;
            case R.id.nav_githublink:
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.github_dapnet)));
                startActivity(browserIntent);
                break;
            case R.id.nav_privacy:
                startActivity(new Intent(this, PrivacyActivity.class));
                // setActionBarTitle("Privacy");
                // ft.replace(R.id.container, PrivacyFragment.newInstance(false)).addToBackStack("PRIVACY").commit();
                break;
            default:
                break;
        }
        drawer = findViewById(R.id.drawer_layout);
        // item.setChecked(true)
        if (!isDrawerLocked) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }
    
    public boolean onNavHeaderSelected() {
        Log.i(TAG, "method: onNavHeaderSelected");

        setActionBarTitle("DAPNET");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.container, WelcomeFragment.newInstance(loggedIn));
        ft.commit();
        NavigationView navigationView = findViewById(R.id.nav_view);
        // TODO: Find which item is checked and uncheck it
        navigationView.getMenu().findItem(R.id.nav_calls).setChecked(false);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (!isDrawerLocked) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    private void setVersion() {
        DAPNET dapnet = DapnetSingleton.getInstance().getDapnet();
        dapnet.getVersion(new DapnetListener<Version>() {
            @Override
            public void onResponse(DapnetResponse<Version> dapnetResponse) {
                if (dapnetResponse.isSuccessful()) {
                    Log.i(TAG, "Connection was successful");
                    try {
                        TextView mNavHeadVersions = findViewById(R.id.navheadversions);
                        mNavHeadVersions.setText(getString(R.string.app_v_core_v_api_v, BuildConfig.VERSION_NAME,
                            dapnetResponse.body().getCore(), dapnetResponse.body().getApi()));
                    }
                    catch (Exception e) {
                        Log.e(TAG,"Error setting versions");
                    }
                }
                else {
                    // TODO: implement .code, .message, snackbar
                    Log.e(TAG, "Error obtaining DAPNET version.");
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                // something went completely wrong (e.g. no internet connection)
                Log.e(TAG, "Fatal connection error.. " + throwable.getMessage());
                Snackbar.make(findViewById(R.id.container), "Fatal connection error.. " + throwable.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });

        mNavHeadVersions = findViewById(R.id.navheadversions);
        if (mNavHeadVersions != null) {
            mNavHeadVersions.setText(getString(R.string.app_v, BuildConfig.VERSION_NAME));
        }
    }

    private void genericSnackbar(String message) {
        Snackbar.make(findViewById(R.id.container), message, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    public void onNavHeaderSelected(View view) {
        onNavHeaderSelected();
    }

    public void setActionBarTitle(String title) {
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
    }

}
