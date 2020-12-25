package de.hampager.dapnetmobile.activites;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.hampager.dapnetmobile.R;

/**
 * PrivacyActivity : Adds View components dynamically.
 * TODO: replace launch from SplashActivity with PrivacyFragment instead
 */
public class PrivacyActivity extends AppCompatActivity {

    ImageView logoImageView;
    LinearLayout privacyLinearLayout;
    Button acceptButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        privacyLinearLayout = findViewById(R.id.privacyLinearLayout);

        // Add ImageView dynamically
        logoImageView = new ImageView(this);
        logoImageView.setImageResource(R.mipmap.ic_afu_dapnet_logo);
        privacyLinearLayout.addView(logoImageView);

        // Populate LinearLayout
        for (String s : getResources().getStringArray(R.array.privacy)) {
            TextView tv = new TextView(this);
            tv.setText(Html.fromHtml(s));
            tv.setMovementMethod(LinkMovementMethod.getInstance()); // enables interaction with HTML link
            privacyLinearLayout.addView(tv);
        }

        // Add Button dynamically
        acceptButton = new Button(this);
        acceptButton.setText(R.string.action_accept);
        acceptButton.setTextColor(getResources().getColor(R.color.white));
        acceptButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        acceptButton.setOnClickListener(view -> {
            // Save preferences
            SharedPreferences.Editor prefEditor = getSharedPreferences(MainActivity.SP, Context.MODE_PRIVATE).edit();
            prefEditor.putBoolean("privacy_activity_executed", true);
            prefEditor.apply();

            // Send user to MainActivity
            startActivity(new Intent(PrivacyActivity.this, MainActivity.class));
            finish();
        });
        privacyLinearLayout.addView(acceptButton);
    }

    /**
     * Exits application.
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        finishAffinity();
        finish();
    }

} // End of class PrivacyActivity
