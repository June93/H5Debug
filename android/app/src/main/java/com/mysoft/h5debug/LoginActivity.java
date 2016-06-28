package com.mysoft.h5debug;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.mysoft.util.ToastUtil;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText username;
    private EditText passwoprd;
    private Button login;
    private Button register;

    private String usernameStr = "";
    private String passwordStr = "";

    public static final int REGISTER_SUCCESS = 001;
    public static final int REGISTER_FAIL = 002;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REGISTER_SUCCESS:
                    ToastUtil.showToastDefault(getBaseContext(), "注册成功");
                    break;
                case REGISTER_FAIL:
                    ToastUtil.showToastDefault(getBaseContext(), "注册失败");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        username = (EditText) findViewById(R.id.username);
        passwoprd = (EditText) findViewById(R.id.passwoprd);
        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(this);
        register = (Button) findViewById(R.id.register);
        register.setOnClickListener(this);
    }

    private boolean isUsernameLegal(String usernameStr) {
        return Pattern.matches("^[a-z]+$", usernameStr);
    }

    private boolean isPasswordLegal(String passwordStr) {
        return true;
    }

    private boolean isInputLegal(String usernameStr, String passwordStr) {
        return isUsernameLegal(usernameStr) && isPasswordLegal(passwordStr);
    }

    private void getViewInput() {
        usernameStr = username.getText().toString().toLowerCase(Locale.getDefault()).trim();
        passwordStr = passwoprd.getText().toString().toLowerCase(Locale.getDefault()).trim();
    }

    private void startLogin() {
        getViewInput();
        if (isInputLegal(usernameStr, passwordStr)) {
            EMClient.getInstance().login(usernameStr, passwordStr, new EMCallBack() {//回调
                @Override
                public void onSuccess() {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            EMClient.getInstance().groupManager().loadAllGroups();
                            EMClient.getInstance().chatManager().loadAllConversations();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }

                @Override
                public void onProgress(int progress, String status) {

                }

                @Override
                public void onError(int code, String message) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            ToastUtil.showToastDefault(getBaseContext(), "登录失败,帐号密码填写错误或者不存在！");
                        }
                    });
                }
            });
        } else {
            ToastUtil.showToastDefault(getBaseContext(), "输入不合法");
        }
    }

    private void startRegister() {
        getViewInput();
        if (isInputLegal(usernameStr, passwordStr)) {
            //注册失败会抛出HyphenateException
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().createAccount(usernameStr, passwordStr);//同步方法
                        handler.sendEmptyMessage(REGISTER_SUCCESS);
                    } catch (HyphenateException e) {
                        handler.sendEmptyMessage(REGISTER_FAIL);
                    }
                }
            }
            ).start();
        } else {
            ToastUtil.showToastDefault(getBaseContext(), "输入不合法");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                startLogin();
                break;
            case R.id.register:
                startRegister();
                break;
        }
    }
}
