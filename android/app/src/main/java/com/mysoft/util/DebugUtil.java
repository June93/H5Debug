package com.mysoft.util;

import android.webkit.WebView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;

/**
 * Created by June on 2016/6/27.
 */
public class DebugUtil {

    public static void login(final LoginCallBack callBack) {
        EMClient.getInstance().login("android", "123456", new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                if (callBack != null) {
                    callBack.resultCallBack(true);
                }
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                if (callBack != null) {
                    callBack.resultCallBack(false);
                }
            }
        });

    }

    private static void logout(final LoginCallBack callBack) {
        EMClient.getInstance().logout(true, new EMCallBack() {

            @Override
            public void onSuccess() {
                if (callBack != null) {
                    callBack.resultCallBack(true);
                }
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                if (callBack != null) {
                    callBack.resultCallBack(false);
                }
            }
        });
    }

    public interface LoginCallBack {
        void resultCallBack(boolean result);
    }

    public static void sendLog(String content, String toChatUsername) {
        //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        EMMessage message = EMMessage.createTxtSendMessage(content, toChatUsername);
        //发送消息
        EMClient.getInstance().chatManager().sendMessage(message);
    }

    public static void excuteJs(WebView webView, String jsStr) {
        try {
            webView.loadUrl("javascript:" + jsStr);
        } catch (Exception e) {
        }
    }

}
