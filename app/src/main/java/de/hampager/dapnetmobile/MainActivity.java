package de.hampager.dapnetmobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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
import android.widget.FrameLayout;
import android.widget.TextView;

import de.hampager.dap4j.DAPNET;
import de.hampager.dap4j.DapnetSingleton;
import de.hampager.dap4j.callbacks.DapnetListener;
import de.hampager.dap4j.callbacks.DapnetResponse;
import de.hampager.dap4j.models.Version;
import de.hampager.dapnetmobile.fragments.CallFragment;
import de.hampager.dapnetmobile.fragments.HelpFragment;
import de.hampager.dapnetmobile.fragments.MapFragment;
import de.hampager.dapnetmobile.fragments.WelcomeFragment;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    boolean loggedIn = false;
    String mServer;
    private MenuItem mPreviousMenuItem;
    private boolean isDrawerLocked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loggedIn) {
                    Intent myIntent = new Intent(MainActivity.this, PostCallActivity.class);
                    MainActivity.this.startActivity(myIntent);
                } else {
                    Snackbar.make(findViewById(R.id.container), getString(R.string.error_logged_in), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.container, WelcomeFragment.newInstance(loggedIn));
            ft.commit();
        }
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.content_frame);
        if (((ViewGroup.MarginLayoutParams)frameLayout.getLayoutParams()).leftMargin == (int) getResources().getDimension(R.dimen.drawer_size)) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
            drawer.setScrimColor(Color.TRANSPARENT);
            isDrawerLocked = true;
            Log.d(TAG, "Drawer locked");
        }
        if (!isDrawerLocked) {

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
        }



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)&&isDrawerLocked) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            SharedPreferences sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
            loggedIn = sharedPref.getBoolean("isLoggedIn", false);
            super.onBackPressed();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nv = navigationView.getMenu();
        MenuItem mloginstatus = nv.findItem(R.id.nav_loginstatus);
        SharedPreferences sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
        loggedIn = sharedPref.getBoolean("isLoggedIn", false);
        mServer = DapnetSingleton.getInstance().getUrl();

        if (loggedIn) {
            mloginstatus.setTitle(R.string.nav_logout);
            Log.i(TAG, "User is logged in!");
        } else {
            mloginstatus.setTitle(R.string.nav_login);
            Log.i(TAG, "User is not logged in!");
        }
        if (!mServer.equals("http://hampager.de/api/") || !mServer.equals("http://dapnet.db0sda.ampr.org:8080/")) {

        } else {
            boolean clearNet = checkAvailable("http://hampager.de/api/");
            boolean dapNet = checkAvailable("http://dapnet.db0sda.ampr.org:8080/");
            if (clearNet || !dapNet) {
                setIntentServer("http://hampager.de/api/");
            }
        }

        return true;
    }

    private void checkServers(String custom) {
        String clear = "http://hampager.de/api/";
        String dap = "http://dapnet.db0sda.ampr.org:8080/";

        if ((!mServer.equals(clear) || !mServer.equals(dap)) && checkAvailable(custom)) {
            setIntentServer(custom);
        } else {
            if (checkAvailable(dap)) {
                setIntentServer(dap);
            } else {
                setIntentServer(clear);
            }

        }
    }

    private boolean checkAvailable(String server) {
        return true;
    }

    private void setIntentServer(String server) {

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.main, menu);


        return true;
    }

    // Handle action bar item clicks in boolean onOptionsItemSelected(MenuItem item). The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
        if (id == R.id.nav_calls) {
            //Insert Call Fragment with 1 Coloumn
            // ft.replace(R.id.container, HamnetCallFragment.newInstance(1))
            if (loggedIn) {
                ft.replace(R.id.container, CallFragment.newInstance());
            } else {
                Snackbar.make(findViewById(R.id.container), getString(R.string.error_logged_in), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        } else if (id == R.id.nav_map) {
            ft.replace(R.id.container, MapFragment.newInstance());
        } else if (id == R.id.nav_githublink) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/DecentralizedAmateurPagingNetwork"));
            startActivity(browserIntent);
        } else if (id == R.id.nav_feedbacklink) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/DecentralizedAmateurPagingNetwork/DAPNETApp/issues"));
            startActivity(browserIntent);
        } else if (id == R.id.nav_loginstatus) {
            if (loggedIn) {
                SharedPreferences sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.clear();
                editor.apply();
            }
            Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
            myIntent.putExtra("defServer", mServer);
            MainActivity.this.startActivity(myIntent);
        } else if (id == R.id.nav_help) {
            ft.replace(R.id.container, HelpFragment.newInstance());
        }
        ft.commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        // item.setChecked(true)
        if(!isDrawerLocked)
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean onNavHeaderSelected() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.container, WelcomeFragment.newInstance(loggedIn));
        ft.commit();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //TODO: Find which item is checked and uncheck it
        navigationView.getMenu().findItem(R.id.nav_calls).setChecked(false);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(!isDrawerLocked)
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setServer(String server) {
        mServer = server;
        SharedPreferences sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPref.edit();
        edit.putString("defServer", server);
        edit.apply();
    }

    private void setVersion() {
        DAPNET dapnet = DapnetSingleton.getInstance().getDapnet();
        dapnet.getVersion(new DapnetListener<Version>() {
            @Override
            public void onResponse(DapnetResponse<Version> dapnetResponse) {
                //TODO: implement isSuccessful()
                //if (response.isSuccessful()) {
                    Log.i(TAG, "Connection was successful");
                    TextView mNavHeadVersions = (TextView) findViewById(R.id.navheadversions);
                String tmp = "App v" + BuildConfig.VERSION_NAME + ", Core v" + dapnetResponse.body().getCore() + ", API v" + dapnetResponse.body().getApi() + ", ";
                    mNavHeadVersions.setText(tmp);
                /*} else {
                    // APIError error = ErrorUtils.parseError(response)
                    Log.e(TAG, "Error getting versions" + response.code());
                    Log.e(TAG, response.message());
                    Snackbar.make(findViewById(R.id.container), getString(R.string.error_get_versions) + " " + response.code() + " " + response.message(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }*/
            }

            @Override
            public void onFailure(Throwable throwable) {
// something went completely wrong (e.g. no internet connection)
                Log.e(TAG, "Fatal connection error.. " + throwable.getMessage());
                Snackbar.make(findViewById(R.id.container), "Fatal connection error.. " + throwable.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
        TextView mNavHeadVersions = (TextView) findViewById(R.id.navheadversions);
        String s = "";
        s += "App v";
        s += BuildConfig.VERSION_NAME;
        if(mNavHeadVersions!=null)
            mNavHeadVersions.setText(s);
    }

    public void onNavHeaderSelected(View view) {
        onNavHeaderSelected();
    }
}