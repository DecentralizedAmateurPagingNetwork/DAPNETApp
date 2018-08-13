package de.hampager.dapnetmobile;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.squareup.leakcanary.LeakCanary;

import de.hampager.dap4j.DapnetSingleton;


public class DAPNETApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        // Normal app init code...
        SharedPreferences sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
        String url = sharedPref.getString("server", getResources().getString(R.string.ClearNetURL));
        String user = sharedPref.getString("user", "");
        String pass = sharedPref.getString("pass", "");
        sharedPref.getBoolean("admin", false);

        DapnetSingleton dapnetSingleton = DapnetSingleton.getInstance();
        dapnetSingleton.init(url, user, pass);
    }
}
