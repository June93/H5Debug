
package com.mysoft.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import java.io.File;

public class MyAppUtil {
    private final static String TAG = "MyAppUtil";

    public static String getVersionName() {
        String version = "";
        Activity context = ActivityUtil.getActivityManager().getTopActivity();
        if (context != null) {
            try {
                PackageManager manager = context.getPackageManager();
                PackageInfo info = manager.getPackageInfo(getPackageName(context), 0);
                version = info.versionName;
            } catch (Exception e) {
            }
        }
        return version;
    }
    
    public static int getVersionCode() {
        Activity context = ActivityUtil.getActivityManager().getTopActivity();
        int version = -1;
        if (context != null) {
            try {
                PackageManager manager = context.getPackageManager();
                PackageInfo info = manager.getPackageInfo(getPackageName(context), 0);
                version = info.versionCode;
            } catch (Exception e) {
            }
        }
        return version;
    }
    
    public static String getPackageName(Context context) {
        String packageName = "";
        if (context != null) {
            packageName = context.getPackageName();
        }
        return StringUtils.getNoneNullString(packageName);
    }
    
    public static void installApk(Context context, File apkfile) {
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
    
    private static ApplicationInfo getApplicationInfo(Context context) {
        ApplicationInfo ai = null;
        if (context != null) {
            try {
                PackageManager pm = context.getPackageManager();
                ai = pm.getApplicationInfo(getPackageName(context), PackageManager.GET_META_DATA);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ai;
    }
    
    public static String getMetaDataString(Context context, String name, String defaultValue) {
        String value = "";
        ApplicationInfo ai = getApplicationInfo(context);
        if (ai != null && ai.metaData != null) {
            value = ai.metaData.getString(name, defaultValue);
        }
        return value;
    }
    
    public static int getMetaDataInt(Context context, String name, int defaultValue) {
        Integer value = 0;
        ApplicationInfo ai = getApplicationInfo(context);
        if (ai != null && ai.metaData != null) {
            value = ai.metaData.getInt(name, defaultValue);
        }
        return value;
    }
    
    public static boolean getMetaDataBoolean(Context context, String name, boolean defaultValue) {
        boolean value = false;
        ApplicationInfo ai = getApplicationInfo(context);
        if (ai != null && ai.metaData != null) {
            value = ai.metaData.getBoolean(name, defaultValue);
        }
        return value;
    }
}
