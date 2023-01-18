package de.hampager.dapnetmobile;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import de.hampager.dap4j.DapnetSingleton;

public class DAPNETApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
        String url = sharedPref.getString("server", getResources().getString(R.string.ClearNetURL));
        String user = sharedPref.getString("user", "");
        String pass = sharedPref.getString("pass", "");
        sharedPref.getBoolean("admin", false);

        DapnetSingleton dapnetSingleton = DapnetSingleton.getInstance();
        dapnetSingleton.init(url, user, pass);
    }
}
