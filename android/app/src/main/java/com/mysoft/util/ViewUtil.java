
package com.mysoft.util;

import android.annotation.TargetApi;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.TouchDelegate;
import android.view.View;

public class ViewUtil {
    public static void setBackground(View view, Drawable background) {
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
            SDK16.setBackground(view, background);
        } else {
            view.setBackgroundDrawable(background);
        }
    }

    @TargetApi(16)
    static class SDK16 {
        public static void setBackground(View view, Drawable background) {
            view.setBackground(background);
        }

    }

    public static void enlargeClickRect(final View view) {
        enlargeClickRect(view, 15, 12, 15, 12);
    }

    public static void enlargeClickRect(final View view, final int left, final int top, final int right, final int bottom) {
        if (view != null) {
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Rect bounds = new Rect();
                    view.getHitRect(bounds);
                    bounds.top -= DensityUtils.dip2px(top);
                    bounds.right += DensityUtils.dip2px(right);
                    bounds.bottom += DensityUtils.dip2px(bottom);
                    bounds.left -= DensityUtils.dip2px(left);
                    TouchDelegate touchDelegate = new TouchDelegate(bounds, view);
                    final View parent = (View) view.getParent();
                    parent.setTouchDelegate(touchDelegate);
                }
            }, 1000);
        }
    }
}
