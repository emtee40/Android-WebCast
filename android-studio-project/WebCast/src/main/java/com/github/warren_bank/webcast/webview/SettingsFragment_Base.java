package com.github.warren_bank.webcast.webview;

import com.github.warren_bank.webcast.R;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsFragment_Base extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferences();
    }

    protected void addPreferences() {
        addPreferencesFromResource(R.xml.preferences);
    }
}
