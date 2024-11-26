package com.github.warren_bank.webcast.webview;

import com.github.warren_bank.webcast.R;

public class SettingsFragment extends SettingsFragment_Base {

  protected void addPreferences() {
    super.addPreferences();

    addPreferencesFromResource(R.xml.addblock_preferences);
  }

}
