package de.hampager.dapnetmobile.activites;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.squareup.haha.perflib.Main;

import java.util.Timer;
import java.util.TimerTask;

import de.hampager.dapnetmobile.R;

/**
 * SplashActivity : displays app logo upon startup.
 * TODO: MainActivity resources (table, map) should be loading through the duration of the Timer/Task (3 seconds).
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // TimerTask to launch Main- or PrivacyActivity
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // Determine launch of PrivacyActivity - only launches upon first launch of this app on user's device
                SharedPreferences pref = getSharedPreferences(MainActivity.SP, Context.MODE_PRIVATE);
                if (!pref.getBoolean("privacy_activity_executed", false)) {
                    startActivity(new Intent(SplashActivity.this, PrivacyActivity.class));

                    /* TODO: replace with PrivacyFragment?
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, PrivacyFragment.newInstance()).commit(); */
                }
                else {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }
                finish();
            }
        };

        // Timer to execute task after 3 seconds
        Timer t = new Timer();
        t.schedule(task, 3000);
    }
}
