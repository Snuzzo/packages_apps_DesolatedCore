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
    private static final String STATUS_BAR_BATTERY_STYLE = "status_bar_battery_style";
    private static final String STATUS_BAR_SHOW_BATTERY_PERCENT = "status_bar_show_battery_percent";
    private static final String STATUS_BAR_CHARGE_COLOR = "status_bar_charge_color";
    private static final String FORCE_CHARGE_BATTERY_TEXT = "force_charge_battery_text";
    private static final String TEXT_CHARGING_SYMBOL = "text_charging_symbol";
    private static final String PREF_STATUS_BAR_WEATHER = "status_bar_weather";
    private static final String PREF_CATEGORY_INDICATORS = "pref_cat_icons";
    private static final String WEATHER_SERVICE_PACKAGE = "org.omnirom.omnijaws";

    private static final int STATUS_BAR_BATTERY_STYLE_PORTRAIT = 0;

    private static final int STATUS_BAR_BATTERY_STYLE_HIDDEN = 4;
    private static final int STATUS_BAR_BATTERY_STYLE_TEXT = 6;

    private ColorPickerPreference mChargeColor;
    private ListPreference mNotificationMode;
    private ListPreference mStatusBarBattery;
    private ListPreference mStatusBarBatteryShowPercent;
    private int mStatusBarBatteryValue;
    private int mStatusBarBatteryShowPercentValue;
    private SwitchPreference mForceChargeBatteryText;
    private ListPreference mTextChargingSymbol;
    private int mTextChargingSymbolValue;
    private ListPreference mStatusBarWeather;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContentResolver resolver = getActivity().getContentResolver();
        title = getResources().getString(R.string.statusbar_settings_title);
        addPreferencesFromResource(R.xml.statusbar_settings);
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

        mForceChargeBatteryText = (SwitchPreference) findPreference(FORCE_CHARGE_BATTERY_TEXT);
        mForceChargeBatteryText.setChecked((Settings.Secure.getInt(resolver,
                Settings.Secure.FORCE_CHARGE_BATTERY_TEXT, 1) == 1));
        mForceChargeBatteryText.setOnPreferenceChangeListener(this);

        mStatusBarBattery = (ListPreference) findPreference(STATUS_BAR_BATTERY_STYLE);
        mStatusBarBatteryValue = Settings.Secure.getInt(resolver,
                Settings.Secure.STATUS_BAR_BATTERY_STYLE, 0);
        mStatusBarBattery.setValue(Integer.toString(mStatusBarBatteryValue));
        mStatusBarBattery.setSummary(mStatusBarBattery.getEntry());
        mStatusBarBattery.setOnPreferenceChangeListener(this);

        int chargeColor = Settings.Secure.getInt(resolver,
                Settings.Secure.STATUS_BAR_CHARGE_COLOR, Color.WHITE);
        mChargeColor = (ColorPickerPreference) findPreference(STATUS_BAR_CHARGE_COLOR);
        mChargeColor.setNewPreviewColor(chargeColor);
        mChargeColor.setOnPreferenceChangeListener(this);

        mStatusBarBatteryShowPercent =
                (ListPreference) findPreference(STATUS_BAR_SHOW_BATTERY_PERCENT);
        mStatusBarBatteryShowPercentValue = Settings.Secure.getInt(resolver,
                Settings.Secure.STATUS_BAR_SHOW_BATTERY_PERCENT, 0);
        mStatusBarBatteryShowPercent.setValue(Integer.toString(mStatusBarBatteryShowPercentValue));
        mStatusBarBatteryShowPercent.setSummary(mStatusBarBatteryShowPercent.getEntry());
        mStatusBarBatteryShowPercent.setOnPreferenceChangeListener(this);

        mTextChargingSymbol = (ListPreference) findPreference(TEXT_CHARGING_SYMBOL);
        mTextChargingSymbolValue = Settings.Secure.getInt(resolver,
                Settings.Secure.TEXT_CHARGING_SYMBOL, 0);
        mTextChargingSymbol.setValue(Integer.toString(mTextChargingSymbolValue));
        mTextChargingSymbol.setSummary(mTextChargingSymbol.getEntry());
        mTextChargingSymbol.setOnPreferenceChangeListener(this);

        enableStatusBarBatteryDependents();

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
            Settings.Secure.putInt(getContentResolver(),
                    Settings.Secure.STATUS_BAR_BATTERY_STYLE, mStatusBarBatteryValue);
            enableStatusBarBatteryDependents();
            return true;
        } else if (preference == mStatusBarBatteryShowPercent) {
            mStatusBarBatteryShowPercentValue = Integer.valueOf((String) newValue);
            int index = mStatusBarBatteryShowPercent.findIndexOfValue((String) newValue);
            mStatusBarBatteryShowPercent.setSummary(
                    mStatusBarBatteryShowPercent.getEntries()[index]);
            Settings.Secure.putInt(getContentResolver(),
                    Settings.Secure.STATUS_BAR_SHOW_BATTERY_PERCENT, mStatusBarBatteryShowPercentValue);
            enableStatusBarBatteryDependents();
            return true;
        } else if  (preference == mForceChargeBatteryText) {
            boolean checked = ((SwitchPreference)preference).isChecked();
            Settings.Secure.putInt(getContentResolver(),
                    Settings.Secure.FORCE_CHARGE_BATTERY_TEXT, checked ? 1:0);
            return true;
        } else if (preference.equals(mChargeColor)) {
            int color = ((Integer) newValue).intValue();
            Settings.Secure.putInt(getContentResolver(),
                    Settings.Secure.STATUS_BAR_CHARGE_COLOR, color);
            return true;
        } else if (preference == mTextChargingSymbol) {
            mTextChargingSymbolValue = Integer.valueOf((String) newValue);
            int index = mTextChargingSymbol.findIndexOfValue((String) newValue);
            mTextChargingSymbol.setSummary(
                    mTextChargingSymbol.getEntries()[index]);
            Settings.Secure.putInt(getContentResolver(),
                    Settings.Secure.TEXT_CHARGING_SYMBOL, mTextChargingSymbolValue);
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
        }
        return false;
    }

    private void enableStatusBarBatteryDependents() {
        if (mStatusBarBatteryValue == STATUS_BAR_BATTERY_STYLE_HIDDEN) {
            mStatusBarBatteryShowPercent.setEnabled(false);
            mForceChargeBatteryText.setEnabled(false);
            mChargeColor.setEnabled(false);
            mTextChargingSymbol.setEnabled(false);
        } else if (mStatusBarBatteryValue == STATUS_BAR_BATTERY_STYLE_TEXT) {
            mStatusBarBatteryShowPercent.setEnabled(false);
            mForceChargeBatteryText.setEnabled(false);
            mChargeColor.setEnabled(false);
            mTextChargingSymbol.setEnabled(true);
        } else if (mStatusBarBatteryValue == STATUS_BAR_BATTERY_STYLE_PORTRAIT) {
            mStatusBarBatteryShowPercent.setEnabled(true);
            mChargeColor.setEnabled(true);
            mForceChargeBatteryText.setEnabled(mStatusBarBatteryShowPercentValue == 2 ? false : true);
            mTextChargingSymbol.setEnabled(true);
        } else {
            mStatusBarBatteryShowPercent.setEnabled(true);
            mChargeColor.setEnabled(true);
            mForceChargeBatteryText.setEnabled(mStatusBarBatteryShowPercentValue == 2 ? false : true);
            mTextChargingSymbol.setEnabled(true);
        }
    }
}

