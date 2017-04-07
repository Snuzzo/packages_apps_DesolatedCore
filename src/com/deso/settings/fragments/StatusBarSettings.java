/*
 * Copyright (C) 2017 Flash ROM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.deso.settings.fragments;

import android.os.Bundle;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference;
import android.os.UserHandle;
import android.support.v7.preference.ListPreference;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import android.provider.Settings;

public class StatusBarSettings extends DesoSettingsFragment implements Preference.OnPreferenceChangeListener {

    private static final String NOTIFICATION_MODE = "notification_mode";

    private ListPreference mNotificationMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getResources().getString(R.string.statusbar_settings_title);
        addPreferencesFromResource(R.xml.statusbar_settings);
        final PreferenceScreen prefScreen = getPreferenceScreen();

        mNotificationMode = (ListPreference) findPreference(NOTIFICATION_MODE);
        mNotificationMode.setOnPreferenceChangeListener(this);
        int headsupMode = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.HEADS_UP_NOTIFICATIONS_USER_ENABLED,
                1, UserHandle.USER_CURRENT);
        int tickerMode = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.STATUS_BAR_SHOW_TICKER,
                0, UserHandle.USER_CURRENT);
        int notificationMode;
        if (headsupMode == 1) {
            notificationMode = 0;
        } else if (tickerMode != 0) {
            notificationMode = tickerMode == 1 ? 1 : 2;
        } else {
            notificationMode = 3;
        }
        mNotificationMode.setValue(String.valueOf(notificationMode));
        mNotificationMode.setSummary(mNotificationMode.getEntry());

    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.equals(mNotificationMode)) {
            int notificationMode = Integer.parseInt(((String) newValue).toString());
            int headsupMode;
            int tickerMode;
            if (notificationMode == 0) {
                headsupMode = 1;
                tickerMode = 0;
            } else if (notificationMode == 1) {
                headsupMode = 0;
                tickerMode = 1;
            } else if (notificationMode == 2) {
                headsupMode = 0;
                tickerMode = 2;
            }else {
                headsupMode = 0;
                tickerMode = 0;
            }
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.HEADS_UP_NOTIFICATIONS_USER_ENABLED, headsupMode, UserHandle.USER_CURRENT);
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.STATUS_BAR_SHOW_TICKER, tickerMode, UserHandle.USER_CURRENT);
            int index = mNotificationMode.findIndexOfValue((String) newValue);
            mNotificationMode.setSummary(
                    mNotificationMode.getEntries()[index]);
            return true;
        }
        return false;
    }
}

