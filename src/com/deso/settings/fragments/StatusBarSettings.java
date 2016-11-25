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

import android.content.ContentResolver;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference;
import android.support.v7.preference.ListPreference;
import android.graphics.Color;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.preference.ColorPickerPreference

public class StatusBarSettings extends DesoSettingsFragment implements Preference.OnPreferenceChangeListener {

    private static final String NOTIFICATION_MODE = "notification_mode";
    private static final String STATUS_BAR_BATTERY_STYLE = "status_bar_battery_style";
    private static final String STATUS_BAR_SHOW_BATTERY_PERCENT = "status_bar_show_battery_percent";
    private static final String STATUS_BAR_CHARGE_COLOR = "status_bar_charge_color";

    private static final int STATUS_BAR_BATTERY_STYLE_PORTRAIT = 0;

    private static final int STATUS_BAR_BATTERY_STYLE_HIDDEN = 4;
    private static final int STATUS_BAR_BATTERY_STYLE_TEXT = 6;

    private ColorPickerPreference mChargeColor;
    private ListPreference mNotificationMode;
    private ListPreference mStatusBarBattery;
    private ListPreference mStatusBarBatteryShowPercent;
    private int mStatusBarBatteryValue;
    private int mStatusBarBatteryShowPercentValue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContentResolver resolver = getActivity().getContentResolver();
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

        mStatusBarBattery = (ListPreference) findPreference(STATUS_BAR_BATTERY_STYLE);
        mStatusBarBatteryValue = Settings.Secure.getInt(resolver,
                Settings.Secure.STATUS_BAR_BATTERY_STYLE, 0);
        mStatusBarBattery.setValue(Integer.toString(mStatusBarBatteryValue));
        mStatusBarBattery.setSummary(mStatusBarBattery.getEntry());
        mStatusBarBattery.setOnPreferenceChangeListener(this);

        int chargeColor = Settings.Secure.getInt(resolver,
                Settings.Secure.STATUS_BAR_CHARGE_COLOR, Color.WHITE);
        mChargeColor = (ColorPickerPreference) findPreference("status_bar_charge_color");
        mChargeColor.setNewPreviewColor(chargeColor);
        mChargeColor.setOnPreferenceChangeListener(this);

        mStatusBarBatteryShowPercent =
                (ListPreference) findPreference(STATUS_BAR_SHOW_BATTERY_PERCENT);
        mStatusBarBatteryShowPercentValue = Settings.Secure.getInt(resolver,
                Settings.Secure.STATUS_BAR_SHOW_BATTERY_PERCENT, 0);
        mStatusBarBatteryShowPercent.setValue(Integer.toString(mStatusBarBatteryShowPercentValue));
        mStatusBarBatteryShowPercent.setSummary(mStatusBarBatteryShowPercent.getEntry());
        mStatusBarBatteryShowPercent.setOnPreferenceChangeListener(this);

        enableStatusBarBatteryDependents(mStatusBarBatteryValue);
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
       } else if (preference.equals(mStatusBarBattery)) {
            mStatusBarBatteryValue = Integer.valueOf((String) newValue);
            int index = mStatusBarBattery.findIndexOfValue((String) newValue);
            mStatusBarBattery.setSummary(
                    mStatusBarBattery.getEntries()[index]);
            Settings.Secure.putInt(resolver,
                    Settings.Secure.STATUS_BAR_BATTERY_STYLE, mStatusBarBatteryValue);
            enableStatusBarBatteryDependents(mStatusBarBatteryValue);
            return true;
        } else if (preference.equals(mStatusBarBatteryShowPercent)) {
            mStatusBarBatteryShowPercentValue = Integer.valueOf((String) newValue);
            int index = mStatusBarBatteryShowPercent.findIndexOfValue((String) newValue);
            mStatusBarBatteryShowPercent.setSummary(
                    mStatusBarBatteryShowPercent.getEntries()[index]);
            Settings.Secure.putInt(resolver,
                    Settings.Secure.STATUS_BAR_SHOW_BATTERY_PERCENT, mStatusBarBatteryShowPercentValue);
        } else if (preference.equals(mChargeColor)) {
            int color = ((Integer) newValue).intValue();
            Settings.Secure.putInt(resolver,
                    Settings.Secure.STATUS_BAR_CHARGE_COLOR, color);
            return true;
        }

        return true;

    private void enableStatusBarBatteryDependents(int batteryIconStyle) {
        if (batteryIconStyle == STATUS_BAR_BATTERY_STYLE_HIDDEN ||
            batteryIconStyle == STATUS_BAR_BATTERY_STYLE_TEXT) {
            mStatusBarBatteryShowPercent.setEnabled(false);
            mChargeColor.setEnabled(false);
        } else if (batteryIconStyle == STATUS_BAR_BATTERY_STYLE_PORTRAIT) {
            mChargeColor.setEnabled(true);
        } else {
            mStatusBarBatteryShowPercent.setEnabled(true);
            mChargeColor.setEnabled(true);
        }
    }
}

