package com.github.warren_bank.webcast.webview;

import com.github.warren_bank.webcast.R;
import com.github.warren_bank.webcast.webview.BrowserUtils;

import android.content.Context;
import android.content.SharedPreferences;

public class AdBlockSettingsUtils {

    public static boolean getEnableAdBlockPreference(Context context) {
        SharedPreferences prefs = BrowserUtils.getPrefs(context);
        String pref_key         = context.getString(R.string.pref_enableadblock_key);
        String pref_default     = context.getString(R.string.pref_enableadblock_default);
        boolean val_default     = "true".equals(pref_default);

        return prefs.getBoolean(pref_key, val_default);
    }

}
