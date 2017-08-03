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

public class BatteryStyles extends DesoSettingsFragment implements Preference.OnPreferenceChangeListener {

    private static final String STATUS_BAR_BATTERY_STYLE = "status_bar_battery_style";
    private static final String STATUS_BAR_SHOW_BATTERY_PERCENT = "status_bar_show_battery_percent";
    private static final String STATUS_BAR_CHARGE_COLOR = "status_bar_charge_color";
    private static final String FORCE_CHARGE_BATTERY_TEXT = "force_charge_battery_text";
    private static final String TEXT_CHARGING_SYMBOL = "text_charging_symbol";
    private static final String PULSE_CHARGING_DURATION = "pulse_charge_duration";


    private static final int STATUS_BAR_BATTERY_STYLE_PORTRAIT = 0;
    private static final int STATUS_BAR_BATTERY_STYLE_HIDDEN = 4;
    private static final int STATUS_BAR_BATTERY_STYLE_TEXT = 6;

    private ColorPickerPreference mChargeColor;
    private ListPreference mStatusBarBattery;
    private ListPreference mStatusBarBatteryShowPercent;
    private int mStatusBarBatteryValue;
    private int mStatusBarBatteryShowPercentValue;
    private SwitchPreference mForceChargeBatteryText;
    private ListPreference mTextChargingSymbol;
    private int mTextChargingSymbolValue;
    private ListPreference mPulseCharge;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContentResolver resolver = getActivity().getContentResolver();
        title = getResources().getString(R.string.battery_settings_title);
        addPreferencesFromResource(R.xml.batterystyles_settings);
        Resources systemUiResources;

        try {
            systemUiResources = getPackageManager().getResourcesForApplication("com.android.systemui");
        } catch (Exception e) {
            return;
        }

        final PreferenceScreen prefScreen = getPreferenceScreen();

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

        mPulseCharge = (ListPreference) findPreference(PULSE_CHARGING_DURATION);
        mPulseCharge.setOnPreferenceChangeListener(this);
        int pulseChargeVal = Settings.System.getInt(getContentResolver(),
                Settings.System.PULSE_CHARGING_DURATION, 2000);
        mPulseCharge.setValue(String.valueOf(pulseChargeVal));
        mPulseCharge.setSummary(mPulseCharge.getEntry());

    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.equals(mStatusBarBattery)) {
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
       } else if (preference == mPulseCharge) {
            int pulseVal = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.PULSE_CHARGING_DURATION,
                    pulseVal);
            mPulseCharge.setValue(String.valueOf(pulseVal));
            mPulseCharge.setSummary(mPulseCharge.getEntry());
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

