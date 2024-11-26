package com.github.warren_bank.webcast.webview;

import com.github.warren_bank.webcast.R;
import com.github.warren_bank.webcast.webview.DownloadHelper;

import android.content.Context;

public class BrowserConfigs {

    public static String getDefaultBookmarks(Context context) {
        return DownloadHelper.readInputStreamString(
            context.getResources().openRawResource(R.raw.default_bookmarks_json)
        );
    }

}
