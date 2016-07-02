package com.mysoft.util;

import java.util.Locale;

/**
 * Created by June on 2016/7/1.
 */
public class CustomCommandUtil {

    private static final String SCREEN_SHOT = "control:screenshot";

    public static void excuteCommand(final String command) {
        String commandTemp = command.toLowerCase(Locale.getDefault()).replaceAll("\\s*", "");
        switch (commandTemp) {
            case SCREEN_SHOT:
                ScreenShortUtil.sendScreenShot();
                break;
            default:
                DebugUtil.sendLog("无效命令", "web");
                break;
        }
    }
}
