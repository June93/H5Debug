package com.mysoft.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import java.util.List;
import java.util.Stack;

/**
 * Created by June on 2016/4/27.
 */
public class ActivityUtil {
    private Stack<Activity> activityStack;

    private static ActivityUtil instance;

    public static final Object lock = new Object();

    private ActivityUtil() {
    }

    public static ActivityUtil getActivityManager() {
        if (instance == null) {
            instance = new ActivityUtil();
        }
        return instance;
    }

    public void popActivity(Activity activity) {
        synchronized (lock) {
            activityStack.remove(activity);
        }
    }

    public void pushActivity(Activity activity) {
        synchronized (lock) {
            if (activityStack == null) {
                activityStack = new Stack<Activity>();
            }
            activityStack.push(activity);
        }
    }

    public Stack<Activity> getActivities() {
        synchronized (lock) {
            return activityStack;
        }
    }

    public Activity getTopActivity() {
        synchronized (lock) {
            Activity topActivity = null;
            Stack<Activity> activitys = getActivities();
            if (activitys != null) {
                topActivity = activitys.peek();
            }
            return topActivity;
        }
    }

    public void finishAllActivitys() {
        synchronized (lock) {
            Stack<Activity> activities = getActivities();
            if (activities != null) {
                for (int n = activities.size() - 1; n >= 0; n--) {
                    Activity activity = activities.get(n);
                    activity.finish();
                }
            }
        }
    }

    public boolean isTopActivity(Context context, String tag) {
        boolean isTop = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        if (cn.getClassName().equalsIgnoreCase(tag)) {
            isTop = true;
        }
        return isTop;
    }

    public static boolean isAppOnForeground(Context context) {
        if (context != null) {
            ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
            String packageName = context.getApplicationContext().getPackageName();
            List<ActivityManager.RunningAppProcessInfo> appProcesses = null;
            if (activityManager != null) {
                appProcesses = activityManager.getRunningAppProcesses();
            }
            if (appProcesses != null) {
                for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                    if (appProcess.processName.equals(packageName) && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
