package com.mysoft.h5debug;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.mysoft.util.DebugUtil;
import com.mysoft.util.ListUtil;
import com.mysoft.util.StringUtils;
import com.mysoft.util.ToastUtil;
import com.mysoft.util.UrlUtil;

import java.util.List;

public class MainActivity extends BaseActivity {

    private WebView webView;

    private WebSettings webSettings;

    private final String H5_DEBUG_SCHEME = "h5debug.69b52e5001736ac7";

    private String url = "";

    private final String NORMAL_URL = "http://10.5.103.69:8081/h5debug/phone/index.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWebView();
        loadPage();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void loadPage() {
        if (isStratFromH5Page()) {
            //登录
            DebugUtil.login(new DebugUtil.LoginCallBack() {
                @Override
                public void resultCallBack(boolean result) {
                    if (result) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                ToastUtil.showToastDefault(getBaseContext(), "连接成功");
                                EMClient.getInstance().groupManager().loadAllGroups();
                                EMClient.getInstance().chatManager().loadAllConversations();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                ToastUtil.showToastDefault(getBaseContext(), "连接失败");
                            }
                        });
                    }
                }
            });
        } else {
            url = NORMAL_URL;
        }
        loadUrl(url);
    }

    private boolean isStratFromH5Page() {
        boolean result = false;
        Intent intent = getIntent();
        if (intent != null) {
            String scheme = intent.getScheme();
            if (H5_DEBUG_SCHEME.equalsIgnoreCase(scheme)) {
                result = true;
                //同时解Url
                Uri uri = intent.getData();
                if (uri != null) {
                    url = uri.getQueryParameter("targetUrl");
                    url = UrlUtil.decodeUrl(url);
                    if (StringUtils.isNull(url)) {
                        url = NORMAL_URL;
                    }
                }
            }
        }
        return result;
    }

    private void initWebView() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });
        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webView.setWebChromeClient(webChromeClient);

        EMClient.getInstance().chatManager().addMessageListener(msgListener);

    }

    private void loadUrl(String url) {
        webView.loadUrl(url);
    }

    private WebChromeClient webChromeClient = new WebChromeClient() {

        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            DebugUtil.sendLog(message, "web");
            return false;
        }

        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            DebugUtil.sendLog(consoleMessage.message(), "web");
            return false;
        }

    };


    EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            //收到消息
            if (!ListUtil.isEmpty(messages)) {
                EMMessage emMessage = messages.get(0);
                if (emMessage != null && emMessage.getType() == EMMessage.Type.TXT) {
                    EMTextMessageBody emMessageBody = (EMTextMessageBody) emMessage.getBody();
                    String jsStr = "";
                    if (emMessageBody != null) {
                        jsStr = emMessageBody.getMessage();
                    }
                    DebugUtil.excuteJs(webView, jsStr);
                }
            }
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            //收到透传消息
        }

        @Override
        public void onMessageReadAckReceived(List<EMMessage> messages) {
            //收到已读回执
        }

        @Override
        public void onMessageDeliveryAckReceived(List<EMMessage> message) {
            //收到已送达回执
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            //消息状态变动
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (msgListener != null) {
            EMClient.getInstance().chatManager().removeMessageListener(msgListener);
            msgListener = null;
        }
    }
}
