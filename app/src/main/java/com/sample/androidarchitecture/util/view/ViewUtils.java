package com.sample.androidarchitecture.util.view;

import android.content.Context;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by trile on 07/07/2017.
 */

public class ViewUtils {

    /**
     * Dismiss keyboard.
     *
     * @param windowToken The {@link IBinder}
     * @param activity The FragmentActivity
     */
    public static void dismissKeyboard(IBinder windowToken, FragmentActivity activity) {
        if (activity != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(windowToken, 0);
        }
    }
}
