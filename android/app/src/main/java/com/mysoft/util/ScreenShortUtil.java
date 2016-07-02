package com.mysoft.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;

import com.mysoft.h5debug.MainActivity;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by June on 2016/6/30.
 */
public class ScreenShortUtil {

    public static void sendScreenShot() {
        Bitmap bitmap = screenShot();
        String filePath = saveBitmap(bitmap);
        if (FileUtil.isFileExists(filePath)) {
            DebugUtil.sendImage(filePath, "web");
        } else {
            DebugUtil.sendLog("截图失败", "web");
        }
    }

    public static Bitmap screenShot() {
        Bitmap bitmap = null;
        Activity topActivity = ActivityUtil.getActivityManager().getTopActivity();
        if (topActivity != null && topActivity instanceof MainActivity) {
            View v = ((MainActivity) topActivity).getWebView().getRootView();
            v.setDrawingCacheEnabled(true);
            v.buildDrawingCache();
            bitmap = v.getDrawingCache();
        }
        return bitmap;
    }

    public static String saveBitmap(Bitmap bm) {
        Activity topActivity = ActivityUtil.getActivityManager().getTopActivity();
        String filePath = "";
        if (bm != null && topActivity != null) {
            filePath = FileUtil.getStoragePath(topActivity) + File.separator + System.currentTimeMillis() + ".png";
            File file = new File(filePath);
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            try {
                FileOutputStream out = new FileOutputStream(file);
                bm.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return filePath;
    }

}
