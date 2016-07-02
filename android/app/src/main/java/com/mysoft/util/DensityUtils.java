
package com.mysoft.util;

import android.content.Context;
import android.util.DisplayMetrics;

public class DensityUtils {
    
    public static int screenHeight = 0;
    
    public static int screenWidth = 0;
    
    public static float screenDensity = 1;
    
    public static float scaledDensity = 1;
    
    public static void init(Context context) {
        if (context != null) {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            if (dm != null) {
                screenDensity = dm.density;
                scaledDensity = dm.scaledDensity;
                screenHeight = dm.heightPixels;
                screenWidth = dm.widthPixels;
            }
        }
    }
    
    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     * 
     * @param pxValue
     * @return
     */
    public static int px2dip(float pxValue) {
        return (int)(pxValue / screenDensity + 0.5f);
    }
    
    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     * 
     * @param dipValue
     * @return
     */
    public static int dip2px(float dipValue) {
        return (int)(dipValue * screenDensity + 0.5f);
    }
    
    /**
     * 将px值转换为sp值，保证文字大小不变
     * 
     * @param pxValue
     * @return
     */
    public static int px2sp(float pxValue) {
        return (int)(pxValue / scaledDensity + 0.5f);
    }
    
    /**
     * 将sp值转换为px值，保证文字大小不变
     * 
     * @param spValue
     * @return
     */
    public static int sp2px(float spValue) {
        return (int)(spValue * scaledDensity + 0.5f);
    }
    
    public static int getScreenWidth() {
        return screenWidth;
    }
    
    public static int getScreenHeight() {
        return screenHeight;
    }
    
}
