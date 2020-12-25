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
import de.hampager.dapnetmobile.listeners.FragmentInteractionListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentInteractionListener, MapFragment.OnWelcomeFragmentListener {

    private static final String TAG = "MainActivity";
    public static final String SP = "sharedPref";

    private TextView mNavHeadVersions;
    private Toolbar toolbar;
    private FrameLayout frameLayout;
    private Button fab; // a "FloatingActionButton"; TODO: may replace with FAB or ExtendedFAB (req. AndroidX migration)
    private DrawerLayout drawer;
    private NavigationView navigationView;

    private boolean loggedIn; // Flag for login status
    private String mServer;

    private boolean isDrawerLocked = false;
    private boolean isMapFull = false; // Flag for WelcomeFragment#setMapFull(boolean)

    private MenuItem mPreviousMenuItem;
    private MenuItem loginMenuItem, loginStatusMenuItem;
    private WelcomeFragment welcomeFragment;

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
                this.startActivity(new Intent(MainActivity.this, PostCallActivity.class));
            } else {
                genericSnackbar(getString(R.string.error_logged_in));
            }
        });

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Populate container with WelcomeFragment
        if (savedInstanceState == null) {
            this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, WelcomeFragment.newInstance(loggedIn), "WelcomeFragment")
                    .commit();
        }
        else {
            this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, welcomeFragment, "WelcomeFragment")
                    .commit();
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

        // Check login status
        if (savedInstanceState == null) {
            loggedIn = getSharedPreferences(SP, Context.MODE_PRIVATE).getBoolean("isLoggedIn", false);
            // User is not logged in- start LoginActivity
            if (!loggedIn) {
                startActivity(new Intent(this, LoginActivity.class));
            }
        }
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "method: onBackPressed");

        drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START) && isDrawerLocked) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            loggedIn = getSharedPreferences(SP, Context.MODE_PRIVATE).getBoolean("isLoggedIn", false);
            super.onBackPressed();
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

        setLoginMenuItems(loggedIn); // "Log in"/"Log out" menu items in nav drawer and top action bar
        return true;
    }

    @Override
    public void onResume() {
        Log.i(TAG, "method: onResume");
        welcomeFragment = (WelcomeFragment) getSupportFragmentManager().findFragmentByTag("WelcomeFragment");
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

        if (mPreviousMenuItem != null && !(mPreviousMenuItem.equals(item))) {
            mPreviousMenuItem.setChecked(false);
        }
        mPreviousMenuItem = item;

        // Navigation item selection
        int id = item.getItemId();
        //item.setCheckable(true);
        //item.setChecked(true);
        switch (id) {
            case R.id.nav_home:
                goToWelcomeFragment();
                break;
            case R.id.nav_calls:
                if (loggedIn) {
                    setFragment(new CallFragment(), "CALLS");
                } else {
                    genericSnackbar(getString(R.string.error_logged_in));
                }
                break;
            case R.id.nav_subscribers:
                if (loggedIn) {
                    setFragment(TableFragment.newInstance(TableFragment.TableTypes.SUBSCRIBERS), "SUBSCRIBERS");
                } else {
                    genericSnackbar(getString(R.string.error_logged_in));
                }
                break;
            /*
            case R.id.nav_map:
                if (!isWelcomeFragmentVisible()) {
                    goToWelcomeFragment(); // Go to WelcomeFragment if it is not currently displayed
                }
                isMapFull = welcomeFragment.setMapFull(true); // Set map to fill container
                break;
             */
            case R.id.nav_transmitters:
                setFragment(TableFragment.newInstance(TableFragment.TableTypes.TRANSMITTERS), "TRANSMITTERS");
                break;
            case R.id.nav_transmitterGroups:
                setFragment(TableFragment.newInstance(TableFragment.TableTypes.TRANSMITTER_GROUPS), "TRANSMITTER_GROUPS");
                break;
            case R.id.nav_rubrics:
                if (loggedIn) {
                    setFragment(TableFragment.newInstance(TableFragment.TableTypes.RUBRICS), "RUBRICS");
                } else {
                    genericSnackbar(getString(R.string.error_logged_in));
                }
                break;
            case R.id.nav_nodes:
                if (loggedIn) {
                    setFragment(TableFragment.newInstance(TableFragment.TableTypes.NODES), "NODES");
                } else {
                    genericSnackbar(getString(R.string.error_logged_in));
                }
                break;
            case R.id.nav_users:
                if (loggedIn) {
                    setFragment(TableFragment.newInstance(TableFragment.TableTypes.USERS), "USERS");
                } else {
                    genericSnackbar(getString(R.string.error_logged_in));
                }
                break;
            case R.id.nav_loginstatus:
                SharedPreferences pref = getSharedPreferences(SP, Context.MODE_PRIVATE);
                if (loggedIn) {
                    loggedIn = false; // Update flag
                    pref.edit().clear() // Clear preferences
                            .putBoolean("privacy_activity_executed", true) // user has already seen PrivacyActivity
                            .putBoolean("isLoggedIn", false) // user is logging off
                            .apply();
                }
                setLoginMenuItems(loggedIn); // update menu items (nav. drawer item to state "Log in" and show nav. bar item)
                startActivity(new Intent(MainActivity.this, LoginActivity.class).putExtra("defServer", mServer));
                break;
            case R.id.nav_help:
                setFragment(new HelpFragment(), "HELP");
                break;
            case R.id.nav_feedbacklink:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.github_feedback))));
                break;
            case R.id.nav_githublink:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.github_dapnet))));
                break;
            case R.id.nav_privacy:
                //startActivity(new Intent(this, PrivacyActivity.class));
                setFragment(PrivacyFragment.newInstance(false), "PRIVACY");
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

        goToWelcomeFragment();

        NavigationView navigationView = findViewById(R.id.nav_view);
        // TODO: Find which item is checked and uncheck it
        navigationView.getMenu().findItem(R.id.nav_calls).setChecked(false);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (!isDrawerLocked) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    /** Communicates with DAPNET server to set version information on the navigation head. */
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
                genericSnackbar("Fatal connection error.. " + throwable.getMessage());
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

    /**
     * The "FloatingActionButton" is preferably hidden when selecting menu items that correspond to Fragments that
     * display information (Tx/groups, users, subscribers, ..., help and privacy pages).
     *
     * @param visible  Flag for "FloatingActionButton" visibility
     * @param title  The title of the Fragment
     */
    private void setFABandTitle(boolean visible, String title) {
        this.setActionBarTitle(title);
        fab.setVisibility((visible) ? View.VISIBLE : View.GONE);
    }

    /**
     * Updates "Log in"/"Log out" menu item on left navigation drawer and shows/hides "Log in" menu item on top
     * navigation bar.
     *
     * @param loggedIn  Flag set when user logs in/out
     */
    private void setLoginMenuItems(boolean loggedIn) {
        Log.i(TAG, (loggedIn) ? "User is logged in!" : "User is not logged in!");
        loginStatusMenuItem.setTitle(((loggedIn) ? R.string.nav_logout : R.string.nav_login));
        loginMenuItem.setVisible(!loggedIn);
    }

    /**
     * Used to check if WelcomeFragment is the current Fragment before setting the map's visibility.
     *
     * @return true if WelcomeFragment is the active frame, false otherwise
     */
    private boolean isWelcomeFragmentVisible() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);
        Log.i(TAG, "isWelcomeFragmentVisible=" + (currentFragment instanceof WelcomeFragment));
        return (currentFragment instanceof WelcomeFragment);
    }

    /**
     * Adds the given Fragment to the stack.
     *
     * @param fragment  The new fragment to place in the main container
     * @param tag   Optional tag name for the fragment,
     *              to later retrieve the fragment with FragmentManager#findFragmentByTag(String).
     */
    private void setFragment(Fragment fragment, String tag) {
        Log.i(TAG, "setFragment=" + tag);
        this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(tag)
                .commit();
    }

    private void goToWelcomeFragment() {
        setFABandTitle(true, "DAPNET");
        setFragment(welcomeFragment, "WelcomeFragment");
    }

    /**
     * FragmentInteractionListener method to set the "FAB" and action bar title to those specified by the
     * given fragment.
     *
     * @param fabVisible  Shows/hides "FloatingActionButton"
     * @param titleID  Name for action bar title
     */
    @Override
    public void onFragmentInteraction(boolean fabVisible, int titleID) {
        Log.i(TAG, "method: onFragmentInteraction");
        setFABandTitle(fabVisible, getString(titleID));
    }

    /**
     *  Implementation for MapFragment.OnWelcomeFragmentListener.
     *  To communicate between MapFragment and MainActivity+WelcomeFragment;
     *  MainActivity responds through OnWelcomeFragmentListener.setMapFull(boolean)
     *  to call WelcomeFragment.setMapFull(boolean).
     *
     * @param fullscreenChecked  Flag to set WelcomeFragment map to fullscreen
     * @return fullscreenChecked
     */
    @Override
    public boolean setMapFull(boolean fullscreenChecked) {
        welcomeFragment.setMapFull(fullscreenChecked);
        return false;
    }

}
