package com.mysoft.h5debug;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.mysoft.util.DebugUtil;
import com.mysoft.util.ListUtil;
import com.mysoft.util.MyAppUtil;
import com.mysoft.util.StringUtils;
import com.mysoft.util.ToastUtil;
import com.mysoft.util.UrlUtil;
import com.mysoft.util.ViewUtil;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends BaseActivity {

    private WebView webView;

    private WebSettings webSettings;

    private final String H5_DEBUG_SCHEME = "h5debug.69b52e5001736ac7";

    private String url = "";

    private final String NORMAL_URL = "http://10.5.103.69:8081/h5debug/phone/index.html";

    private TextView tip;

    private Animation alpha;

    private TextView refresh;

    private TextView title;

    protected ProgressBar progressBar;

    private AtomicInteger progressCount = new AtomicInteger();

    private Handler handler = new Handler();

    private TextView goBack;

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
                                refresh.setVisibility(View.VISIBLE);
                                ToastUtil.showToastDefault(getBaseContext(), "连接成功");
                                //弹框
                                tip.setVisibility(View.VISIBLE);
                                tip.startAnimation(alpha);
                                //发送设备信息重新加载页面
                                DebugUtil.sendLog(initDeviceInfo(), "web");
                                loadUrl();
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
        loadUrl();
    }

    private String initDeviceInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("【设备名称:").append(Build.MODEL).append("，")
                .append("系统版本:").append(Build.VERSION.RELEASE).append("，")
                .append("APP版本:").append(MyAppUtil.getVersionName()).append("，")
                .append("设备ID:").append(Build.SERIAL)
                .append("】");
        return sb.toString();
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

    private void initTipView() {
        tip = (TextView) findViewById(R.id.tip);
        ViewUtil.enlargeClickRect(tip);
        tip.setVisibility(View.GONE);
        tip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DebugUtil.sendLog("退出调试模式", "web");
                refresh.setVisibility(View.GONE);
                DebugUtil.logout(new DebugUtil.LoginCallBack() {
                    @Override
                    public void resultCallBack(boolean result) {
                        if (result) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (tip != null) {
                                        alpha.cancel();
                                        tip.clearAnimation();
                                        tip.setVisibility(View.GONE);
                                        tip.invalidate();
                                    }
                                    ToastUtil.showToastDefault(getBaseContext(), "退出调试模式");
                                }
                            });
                        }
                    }
                });
            }
        });
        alpha = AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_alpha);
    }

    private void initWebView() {
        initTipView();
        initRefreshView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        webView = (WebView) findViewById(R.id.webView);
        title = (TextView) findViewById(R.id.title);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                showProgressBar();
                if (goBack.getVisibility() == View.VISIBLE) {
                    goBack.setEnabled(false);
                }
            }

            @Override
            public void onPageFinished(WebView view, String myUrl) {
                super.onPageFinished(view, myUrl);
                url = myUrl;
                title.setText(view.getTitle());
                hideProgressBar();
                if (isHaveHistory()) {
                    goBack.setVisibility(View.VISIBLE);
                } else {
                    goBack.setVisibility(View.GONE);
                }
                if (goBack.getVisibility() == View.VISIBLE) {
                    goBack.setEnabled(true);
                }
            }
        });
        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webView.setWebChromeClient(webChromeClient);

        progressBar = (ProgressBar) findViewById(R.id.progress);

        goBack = (TextView) findViewById(R.id.goBack);
        goBack.setVisibility(View.GONE);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goHistory();
            }
        });

        EMClient.getInstance().chatManager().addMessageListener(msgListener);
    }

    private void goHistory() {
        if (isHaveHistory()) {
            webView.goBack();
        }
    }

    private boolean isHaveHistory() {
        return webView.canGoBack();
    }

    protected void showProgressBar() {
        progressCount.set(0);
        progressBar.setVisibility(View.VISIBLE);
        // 利用线程控制进度条的进度
        new Thread(new Runnable() {

            public void run() {
                while (progressCount.get() < 80) {
                    // doWord()模拟一个任务的进度
                    doWork();
                    handler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressCount.get());
                        }
                    });
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private int doWork() {
        return progressCount.incrementAndGet();
    }

    protected void hideProgressBar() {
        new Thread(new Runnable() {

            public void run() {
                while (progressCount.get() <= 100) {
                    // doWord()模拟一个任务的进度
                    doWork();
                    handler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressCount.get());
                            if (progressBar.getProgress() == 100) {
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE);
                                        return;
                                    }
                                });
                            }
                        }
                    });
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void initRefreshView() {
        refresh = (TextView) findViewById(R.id.refresh);
        refresh.setVisibility(View.GONE);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.reload();
            }
        });
    }

    private void loadUrl() {
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
                    String command = "";
                    if (emMessageBody != null) {
                        command = emMessageBody.getMessage();
                    }
                    DebugUtil.excuteCommand(webView, command);
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

    public View getWebView() {
        return webView;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (msgListener != null) {
            EMClient.getInstance().chatManager().removeMessageListener(msgListener);
            msgListener = null;
        }
    }
}
