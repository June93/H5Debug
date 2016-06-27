
package com.mysoft.util;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

public class ToastUtil {
    
    private static Toast mToast = null;
    
    private static Toast mViewToast = null;
    
    private static byte[] lock = new byte[0]; // 特别的instance变量
    
    private static int defaultGravity;
    
    private static int defaultX;
    
    private static int defaultY;
    
    public static void showToastDefault(Context con, int res) {
        synchronized (lock) {
            String text = con.getResources().getString(res);
            showCenter(con, text);
        }
    }
    
    public static void showToastDefault(Context con, String res) {
        synchronized (lock) {
            showCenter(con, res);
        }
    }
    
    public static void showToastDefault(Context con, View view) {
        synchronized (lock) {
            showCenter(con, view);
        }
    }
    
    public static void showToastDefault(Context con, int res, int duration) {
        synchronized (lock) {
            showCenter(con, res, duration);
        }
    }
    
    public static void showToastDefault(Context con, String res, int duration) {
        synchronized (lock) {
            showCenter(con, res, duration);
        }
    }
    
    public static void showToastDefault(Context con, int res, int duration, int gravity) {
        synchronized (lock) {
            showCenter(con, res, duration, gravity);
        }
    }
    
    public static void showToastDefault(Context con, String res, int duration, int gravity) {
        synchronized (lock) {
            showCenter(con, res, duration, gravity);
        }
    }
    
    public static void showToastDefault(Context con, View view, int duration) {
        synchronized (lock) {
            showCenter(con, view, duration);
        }
    }
    
    private static void showCenter(Context con, String res) {
        if (mToast == null) {
            mToast = Toast.makeText(con, res, Toast.LENGTH_SHORT);
            defaultGravity = mToast.getGravity();
            defaultX = mToast.getXOffset();
            defaultY = mToast.getYOffset();
        } else {
            mToast.setText(res);
            mToast.setGravity(defaultGravity, defaultX, defaultY);
        }
        mToast.show();
    }
    
    private static void showCenter(Context con, String res, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(con, res, duration);
            defaultGravity = mToast.getGravity();
            defaultX = mToast.getXOffset();
            defaultY = mToast.getYOffset();
        } else {
            mToast.setText(res);
            mToast.setDuration(duration);
            mToast.setGravity(defaultGravity, defaultX, defaultY);
        }
        mToast.show();
    }
    
    private static void showCenter(Context con, int res, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(con, res, duration);
            defaultGravity = mToast.getGravity();
            defaultX = mToast.getXOffset();
            defaultY = mToast.getYOffset();
        } else {
            mToast.setText(res);
            mToast.setDuration(duration);
            mToast.setGravity(defaultGravity, defaultX, defaultY);
        }
        mToast.show();
    }
    
    private static void showCenter(Context con, String res, int duration, int gravity) {
        if (mToast == null) {
            mToast = Toast.makeText(con, res, duration);
            defaultGravity = mToast.getGravity();
            defaultX = mToast.getXOffset();
            defaultY = mToast.getYOffset();
        } else {
            mToast.setText(res);
            mToast.setDuration(duration);
        }
        mToast.setGravity(gravity, 0, 0);
        mToast.show();
    }
    
    private static void showCenter(Context con, int res, int duration, int gravity) {
        if (mToast == null) {
            mToast = Toast.makeText(con, res, duration);
            defaultGravity = mToast.getGravity();
            defaultX = mToast.getXOffset();
            defaultY = mToast.getYOffset();
        } else {
            mToast.setText(res);
            mToast.setDuration(duration);
        }
        mToast.setGravity(gravity, 0, 0);
        mToast.show();
    }
    
    private static void showCenter(Context con, View view) {
        if (mViewToast == null) {
            mViewToast = new Toast(con);
        }
        mViewToast.setView(view);
        mViewToast.setDuration(Toast.LENGTH_SHORT);
        mViewToast.show();
    }
    
    private static void showCenter(Context con, View view, int duration) {
        if (mViewToast == null) {
            mViewToast = new Toast(con);
        }
        mViewToast.setView(view);
        mViewToast.setDuration(duration);
        mViewToast.setGravity(Gravity.CENTER, 0, 0);
        mViewToast.show();
    }
}
