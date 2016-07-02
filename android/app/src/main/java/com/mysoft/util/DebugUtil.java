package com.mysoft.util;

import android.app.Activity;
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

    public static void logout(final LoginCallBack callBack) {
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

    public static void excuteCommand(final WebView webView, final String command) {
        Activity topActivity = ActivityUtil.getActivityManager().getTopActivity();
        if (topActivity == null) {
            DebugUtil.sendLog("执行命令失败", "web");
            return;
        }
        topActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (StringUtils.getNoneNullString(command).startsWith("control:")) {
                        //执行自己的命令集
                        CustomCommandUtil.excuteCommand(command);
                    } else if (StringUtils.getNoneNullString(command).startsWith("javascript:")) {
                        webView.loadUrl(command);
                    } else {
                        webView.loadUrl("javascript:" + command);
                    }
                } catch (Exception e) {
                    sendLog("执行命令失败", "web");
                    e.printStackTrace();
                }
            }
        });
    }

    public static void sendImage(String imagePath, String toChatUsername) {
        //imagePath为图片本地路径，false为不发送原图（默认超过100k的图片会压缩后发给对方），需要发送原图传true
        EMMessage message = EMMessage.createImageSendMessage(imagePath, false, toChatUsername);
        EMClient.getInstance().chatManager().sendMessage(message);
    }

}
