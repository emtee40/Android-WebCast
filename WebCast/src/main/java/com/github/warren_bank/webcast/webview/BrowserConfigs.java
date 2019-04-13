package com.github.warren_bank.webcast.webview;

import java.util.Map;
import java.util.Hashtable;

public class BrowserConfigs {

    public static Map<String, String> getDefaultBookmarks() {
        Hashtable<String, String> bookmarks = new Hashtable<String, String>();

        bookmarks.put(
            "-- blank --",
            "about:blank"
        );
        bookmarks.put(
            "Google",
            "https://www.google.com/"
        );
        bookmarks.put(
            "DuckDuckGo",
            "https://duckduckgo.com/"
        );
        return bookmarks;
    }

}
