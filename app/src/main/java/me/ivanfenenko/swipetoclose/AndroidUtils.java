package me.ivanfenenko.swipetoclose;

import android.os.Build;

/**
 * Created by klickrent on 20/07/16.
 */

public final class AndroidUtils {
    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
}