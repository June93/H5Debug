package com.mysoft.h5debug;

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
import com.mysoft.util.ToastUtil;

import org.json.JSONException;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private WebView webView;

    private WebSettings webSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWebView();
        loadUrl("http://10.5.103.69:8081/test.html");
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

        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            ToastUtil.showToastDefault(getBaseContext(), message);
            return true;
        }


        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            ToastUtil.showToastDefault(getBaseContext(), message);
            return true;
        }

        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            ToastUtil.showToastDefault(getBaseContext(), consoleMessage.message());
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
                    Log.v("MainActivity", jsStr);
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
