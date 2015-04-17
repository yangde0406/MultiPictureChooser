package com.yangde.multipicturechooser.utils;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by yangde on 15/4/17.
 */
public class Utils {
    /**
     * 获取屏幕尺寸
     *
     * @param context
     * @param screenSize 屏幕尺寸信息：width = screenSize[0]; heigth = screenSize[1];
     */
    public static void GetScreenSize(Context context, int[] screenSize) {
        if (null == screenSize || screenSize.length < 2) {
            screenSize = new int[2];
        }

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        screenSize[0] = displayMetrics.widthPixels;
        screenSize[1] = displayMetrics.heightPixels;
    }
}
