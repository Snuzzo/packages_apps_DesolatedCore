/*
 * Copyright (C) 2017 Flash ROM
 *
71 * Licensed under the Apache License, Version 2.0 (the "License");
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

import android.content.res.Resources;
import android.content.ContentResolver;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference;
import android.support.v7.preference.ListPreference;
import android.graphics.Color;
import android.content.Context;
import android.support.v7.preference.PreferenceCategory;
import android.support.v14.preference.PreferenceFragment;
import android.support.v14.preference.SwitchPreference;
import android.provider.SearchIndexableResource;
import com.android.internal.util.tesla.TeslaUtils;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.preference.ColorPickerPreference;

public class StatusBarSettings extends DesoSettingsFragment implements Preference.OnPreferenceChangeListener {

    private static final String NOTIFICATION_MODE = "notification_mode";
    private static final String PREF_STATUS_BAR_WEATHER = "status_bar_weather";
    private static final String PREF_CATEGORY_INDICATORS = "pref_cat_icons";
    private static final String WEATHER_SERVICE_PACKAGE = "org.omnirom.omnijaws";
    private static final String PREF_HEADS_UP_TIME_OUT = "heads_up_time_out";
    private static final String PREF_HEADS_UP_SNOOZE_TIME = "heads_up_snooze_time";


    private ListPreference mNotificationMode;
    private ListPreference mStatusBarWeather;
    private ListPreference mHeadsUpTimeOut;
    private ListPreference mHeadsUpSnoozeTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContentResolver resolver = getActivity().getContentResolver();
        title = getResources().getString(R.string.statusbar_settings_title);
        addPreferencesFromResource(R.xml.statusbar_settings);
        Resources systemUiResources;

        try {
            systemUiResources = getPackageManager().getResourcesForApplication("com.android.systemui");
        } catch (Exception e) {
            return;
        }

        final PreferenceScreen prefScreen = getPreferenceScreen();

        PreferenceCategory categoryIndicators = (PreferenceCategory) getPreferenceScreen().findPreference(PREF_CATEGORY_INDICATORS);

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


        // Status bar weather
        mStatusBarWeather = (ListPreference) getPreferenceScreen().findPreference(PREF_STATUS_BAR_WEATHER);
        if (mStatusBarWeather != null && (!TeslaUtils.isPackageInstalled(getActivity(),WEATHER_SERVICE_PACKAGE))) {
            categoryIndicators.removePreference(mStatusBarWeather);
        } else {
            int temperatureShow = Settings.System.getIntForUser(resolver,
                Settings.System.STATUS_BAR_SHOW_WEATHER_TEMP, 0,
                UserHandle.USER_CURRENT);
            mStatusBarWeather.setValue(String.valueOf(temperatureShow));
            if (temperatureShow == 0) {
                mStatusBarWeather.setSummary(R.string.statusbar_weather_summary);
            } else {
                mStatusBarWeather.setSummary(mStatusBarWeather.getEntry());
            }
            mStatusBarWeather.setOnPreferenceChangeListener(this);
        }

        int defaultTimeOut = systemUiResources.getInteger(systemUiResources.getIdentifier(
                    "com.android.systemui:integer/heads_up_notification_decay", null, null));
        mHeadsUpTimeOut = (ListPreference) findPreference(PREF_HEADS_UP_TIME_OUT);
        mHeadsUpTimeOut.setOnPreferenceChangeListener(this);
        int headsUpTimeOut = Settings.System.getInt(getContentResolver(),
                Settings.System.HEADS_UP_TIMEOUT, defaultTimeOut);
        mHeadsUpTimeOut.setValue(String.valueOf(headsUpTimeOut));
        updateHeadsUpTimeOutSummary(headsUpTimeOut);

        int defaultSnooze = systemUiResources.getInteger(systemUiResources.getIdentifier(
                    "com.android.systemui:integer/heads_up_default_snooze_length_ms", null, null));
        mHeadsUpSnoozeTime = (ListPreference) findPreference(PREF_HEADS_UP_SNOOZE_TIME);
        mHeadsUpSnoozeTime.setOnPreferenceChangeListener(this);
        int headsUpSnooze = Settings.System.getInt(getContentResolver(),
                Settings.System.HEADS_UP_NOTIFICATION_SNOOZE, defaultSnooze);
        mHeadsUpSnoozeTime.setValue(String.valueOf(headsUpSnooze));
        updateHeadsUpSnoozeTimeSummary(headsUpSnooze);

        updateHeadsUpPref(headsupMode);
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
            updateHeadsUpPref(headsupMode);
            return true;
       } else if (preference == mStatusBarWeather) {
            int temperatureShow = Integer.valueOf((String) newValue);
            int index = mStatusBarWeather.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(getContentResolver(),
                   Settings.System.STATUS_BAR_SHOW_WEATHER_TEMP,
                   temperatureShow, UserHandle.USER_CURRENT);
            if (temperatureShow == 0) {
                mStatusBarWeather.setSummary(R.string.statusbar_weather_summary);
            } else {
                mStatusBarWeather.setSummary(
                mStatusBarWeather.getEntries()[index]);
            }
            return true;
       } else if (preference == mHeadsUpTimeOut) {
            int headsUpTimeOut = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.HEADS_UP_TIMEOUT,
                    headsUpTimeOut);
            updateHeadsUpTimeOutSummary(headsUpTimeOut);
            return true;
       } else if (preference == mHeadsUpSnoozeTime) {
            int headsUpSnooze = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.HEADS_UP_NOTIFICATION_SNOOZE,
                    headsUpSnooze);
            updateHeadsUpSnoozeTimeSummary(headsUpSnooze);
            return true;
       }
       return false;
    }

    private void updateHeadsUpTimeOutSummary(int value) {
        String summary = getResources().getString(R.string.heads_up_time_out_summary,
                value / 1000);
        mHeadsUpTimeOut.setSummary(summary);
    }

    private void updateHeadsUpSnoozeTimeSummary(int value) {
        if (value == 0) {
            mHeadsUpSnoozeTime.setSummary(getResources().getString(R.string.heads_up_snooze_disabled_summary));
        } else if (value == 60000) {
            mHeadsUpSnoozeTime.setSummary(getResources().getString(R.string.heads_up_snooze_summary_one_minute));
        } else {
            String summary = getResources().getString(R.string.heads_up_snooze_summary, value / 60 / 1000);
            mHeadsUpSnoozeTime.setSummary(summary);
        }
    }

    private void updateHeadsUpPref(int value) {
        if (value == 0) {
            mHeadsUpTimeOut.setEnabled(false);
            mHeadsUpSnoozeTime.setEnabled(false);
        } else {
            mHeadsUpTimeOut.setEnabled(true);
            mHeadsUpSnoozeTime.setEnabled(true);
        }
    }
}

