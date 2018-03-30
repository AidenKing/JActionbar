package com.king.app.jactionbar.utils;

import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 *
 * <p/>author：Aiden
 * <p/>create time: 2018/3/27 17:55
 */
public class ParamUtils {

    public static int dp2px(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

}
