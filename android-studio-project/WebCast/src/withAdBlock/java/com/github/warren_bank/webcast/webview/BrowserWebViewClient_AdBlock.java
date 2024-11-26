package com.github.warren_bank.webcast.webview;

import com.github.warren_bank.webcast.R;
import com.github.warren_bank.webcast.webview.AdBlockSettingsUtils;
import com.github.warren_bank.webcast.webview.BrowserUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

public class BrowserWebViewClient_AdBlock extends BrowserWebViewClient_VideoDetector {
    private boolean isEnabled;
    private boolean isPopulatingHosts;
    private TreeMap<String, Object> blockedHosts;

    public BrowserWebViewClient_AdBlock(BrowserActivity browserActivity) {
        super(browserActivity);

        Context context = browserActivity.getApplicationContext();

        updateIsEnabled(context);
        updateBlockedHosts(context);
        addPreferenceChangeListener(context);
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        super.shouldInterceptRequest(view, url);

        return shouldBlockRequest(url);
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        super.shouldInterceptRequest(view, request);

        String url = request.getUrl().toString();
        return shouldBlockRequest(url);
    }

    private void updateIsEnabled(Context context) {
        isEnabled = AdBlockSettingsUtils.getEnableAdBlockPreference(context);
    }

    private void updateBlockedHosts(Context context) {
        blockedHosts = null;

        if (isEnabled) {
            isPopulatingHosts = true;
            populateBlockedHosts(context);
        }
        isPopulatingHosts = false;
    }

    private void populateBlockedHosts(Context context) {
        InputStream is = context.getResources().openRawResource(R.raw.adblock_serverlist);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;

        if (is != null) {
            try {
                blockedHosts = new TreeMap<String, Object>();

                while ((line = br.readLine()) != null) {
                    line = line.toLowerCase().trim();

                    if (!line.isEmpty() && !line.startsWith("#")) {
                        blockedHosts.put(line, null);
                    }
                }
            } catch (IOException e) {
                blockedHosts = null;
            }
        }
    }

    private void addPreferenceChangeListener(Context context) {
        SharedPreferences prefs = BrowserUtils.getPrefs(context);

        prefs.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged (SharedPreferences sharedPreferences, String key) {
                if (key.equals(AdBlockSettingsUtils.getEnableAdBlockPreferenceKey(context))) {
                    updateIsEnabled(context);
                    updateBlockedHosts(context);
                }
            }
        });
    }

    private boolean isHostBlocked(String url) {
        if (!isEnabled || isPopulatingHosts || (blockedHosts == null)) return false;

        try {
            Uri uri = Uri.parse(url);
            String host = uri.getHost().toLowerCase().trim();

            List<String> domains = Arrays.asList(host.split("\\."));
            String domain;

            while (domains.size() > 1) {
                domain = TextUtils.join(".", domains);
                if (blockedHosts.containsKey(domain)) {
                    return true;
                }
                domains = domains.subList(1, domains.size());
            }
        }
        catch(Exception e) {}
        return false;
    }

    private WebResourceResponse shouldBlockRequest(String url) {
        if (isHostBlocked(url)) {
            ByteArrayInputStream EMPTY = new ByteArrayInputStream("".getBytes());
            return new WebResourceResponse("text/plain", "utf-8", EMPTY);
        }
        else {
            return null;
        }
    }
}
