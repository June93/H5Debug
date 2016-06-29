package com.mysoft.h5debug;

import android.app.Activity;
import android.os.Bundle;
import com.mysoft.util.ActivityUtil;

/**
 * Created by June on 2016/4/27.
 */
public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUtil.getActivityManager().pushActivity(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityUtil.getActivityManager().popActivity(this);
    }
}
