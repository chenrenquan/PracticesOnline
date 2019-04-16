package net.lzzy.practicesonline.activities.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Pair;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author lzzy_Colo
 * @date 2019/3/11
 * Description:
 */
public class AppUtils extends Application {
    public static final String SP_SETTING = "spSetting";
    public static final String URL_IP = "urlIp";
    public static final String URL_PORT = "urlPort";
    private static List<Activity> activities = new LinkedList<>();
    private static Context context;
    private static String runningActivity;

    public static ThreadPoolExecutor getExectuor() {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public static void exit() {
        for (Activity activity : activities) {
            if (activity != null) {
                activity.finish();
            }
        }
        System.exit(0);
    }

    public static Activity getRunningActivity() {
        for (Activity activity : activities) {
            String name = activity.getLocalClassName();
            if (AppUtils.runningActivity.equals(name)) {
                return activity;
            }
        }
        return null;
    }

    public static void setRunning(String runningActivity) {
        AppUtils.runningActivity = runningActivity;
    }

    public static void setStopped(String stoppedActivity) {
        if (stoppedActivity.equals(AppUtils.runningActivity)) {
            AppUtils.runningActivity = "";
        }
    }

    public static boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info.isConnected();
    }

    public static void tryConnectServer(String address) throws IOException {
        URL url = new URL(address);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(5000);
        connection.getContent();
    }

    public static void saveServerSetting(String ip, String port, Context context) {
        SharedPreferences spSetting = context.getSharedPreferences(SP_SETTING, MODE_PRIVATE);
        spSetting.edit()
                .putString(URL_IP, ip)
                .putString(URL_PORT, port)
                .apply();
    }

    public static Pair<String, String> lpadServerSetting(Context context) {
        SharedPreferences spSetting = context.getSharedPreferences(SP_SETTING, MODE_PRIVATE);
        String ip = spSetting.getString(URL_IP, "10.88.91.102");
        String port = spSetting.getString(URL_PORT, "8888");
        return new Pair<>(ip, port);
    }
}