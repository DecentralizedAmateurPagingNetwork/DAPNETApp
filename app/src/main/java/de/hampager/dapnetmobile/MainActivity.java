package de.hampager.dapnetmobile;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
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
import android.widget.TextView;

import de.hampager.dapnetmobile.fragments.CallFragment;
import de.hampager.dapnetmobile.fragments.WelcomeFragment;
import de.hampager.dapnetmobile.BuildConfig;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    boolean loggedIn = false;
    private static final String TAG = "MainActivity";

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
                    Snackbar.make(findViewById(R.id.container), "Error. Are you logged in?", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Fragment currentFragment = getFragmentManager().findFragmentById(R.id.container);
        if (currentFragment == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.container, WelcomeFragment.newInstance(loggedIn));
            ft.commit();
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
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

        TextView mNavHeadDAPNET=(TextView)findViewById(R.id.navheaddapnet);
        mNavHeadDAPNET.setText("DAPNET v" +BuildConfig.VERSION_NAME);
        if (loggedIn) {
            mloginstatus.setTitle(R.string.nav_logout);
            Log.i(TAG, "User is logged in!");
        } else {
            mloginstatus.setTitle(R.string.nav_login);
            Log.i(TAG, "User is not logged in!");
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.main, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*TODO: Add Settings
         if (id == R.id.action_settings) {
         return true;
         }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        if (id == R.id.nav_calls) {
            //Insert Call Fragment with 1 Coloumn
            //ft.replace(R.id.container, HamnetCallFragment.newInstance(1));
            if (loggedIn) {
                ft.replace(R.id.container, CallFragment.newInstance());
            } else {
                Snackbar.make(findViewById(R.id.container), "Error. Are you logged in?", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        } else if (id == R.id.nav_githublink) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/DecentralizedAmateurPagingNetwork"));
            startActivity(browserIntent);
        }else if (id == R.id.nav_feedbacklink) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/DecentralizedAmateurPagingNetwork/DAPNETApp/issues"));
            startActivity(browserIntent);
        }
        else if (id == R.id.nav_loginstatus) {
            if (loggedIn) {
                SharedPreferences sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.clear();
                editor.apply();
            }
            Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
            MainActivity.this.startActivity(myIntent);
        }
        ft.commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}