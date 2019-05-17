package com.angcyo.pager.transition.demo;

import android.content.Context;
import android.content.res.Resources;

/**
 * Created by angcyo on 15-12-16 016 15:41 下午.
 */
public class RUtils {

    /**
     * 屏幕 的高度, 包含 DecorView的高度-状态栏-导航栏
     * <p>
     * 当状态栏是透明时, 那么状态栏的高度依然还在.
     */
    public static int getScreenHeight(Context activity) {
        if (activity != null) {
            return activity.getResources().getDisplayMetrics().heightPixels;
        }
        return 0;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static int getScreenWidth(Context activity) {
        if (activity != null) {
            return activity.getResources().getDisplayMetrics().widthPixels;
        }
        return 0;
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }
}
