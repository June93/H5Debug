package com.mysoft.util;

import android.webkit.WebView;

/**
 * Created by June on 2016/6/27.
 */
public class DebugUtil {

    public static void excuteJs(WebView webView, String jsStr) {
        try {
            webView.loadUrl("javascript:" + jsStr);
        } catch (Exception e) {
        }
    }

}
