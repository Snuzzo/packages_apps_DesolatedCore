/*
 * Copyright (C) 2017 Desolation ROM
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

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.preference.ColorPickerPreference;

public class LockscreenSettings extends DesoSettingsFragment implements OnPreferenceChangeListener {

    private static final String LOCKSCREEN_OWNER_INFO_COLOR = "lockscreen_owner_info_color";
    private static final String LOCKSCREEN_ALARM_COLOR = "lockscreen_alarm_color";
    private static final String LOCKSCREEN_CLOCK_COLOR = "lockscreen_clock_color";
    private static final String LOCKSCREEN_CLOCK_DATE_COLOR = "lockscreen_clock_date_color";
    private static final String LOCKSCREEN_COLORS_RESET = "lockscreen_colors_reset";
    private static final String LOCKSCREEN_FP_CATEGORY = "fp_ls_category";
    private static final String LOCKSCREEN_PREF = "ls_preferences";
    private ColorPickerPreference mLockscreenOwnerInfoColorPicker;
    private ColorPickerPreference mLockscreenAlarmColorPicker;
    private ColorPickerPreference mLockscreenClockColorPicker;
    private ColorPickerPreference mLockscreenClockDateColorPicker;
    private Preference mLockscreenColorsReset;
    private FingerprintManager mFPMgr;

    private static final int MENU_RESET = Menu.FIRST;
    static final int DEFAULT = 0xffffffff;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getResources().getString(R.string.lockscreen_settings_title);
        addPreferencesFromResource(R.xml.lockscreen_settings);

        ContentResolver resolver = getActivity().getContentResolver();
        PreferenceScreen mLSPrefScreen = (PreferenceScreen) findPreference(LOCKSCREEN_PREF);
        // LockscreenColors
        int intColor;
        String hexColor;

        mLockscreenOwnerInfoColorPicker = (ColorPickerPreference) findPreference(LOCKSCREEN_OWNER_INFO_COLOR);
        mLockscreenOwnerInfoColorPicker.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(resolver,
                    Settings.System.LOCKSCREEN_OWNER_INFO_COLOR, DEFAULT);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mLockscreenOwnerInfoColorPicker.setSummary(hexColor);
        mLockscreenOwnerInfoColorPicker.setNewPreviewColor(intColor);

        mLockscreenAlarmColorPicker = (ColorPickerPreference) findPreference(LOCKSCREEN_ALARM_COLOR);
        mLockscreenAlarmColorPicker.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(resolver,
                    Settings.System.LOCKSCREEN_ALARM_COLOR, DEFAULT);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mLockscreenAlarmColorPicker.setSummary(hexColor);
        mLockscreenAlarmColorPicker.setNewPreviewColor(intColor);

        mLockscreenClockColorPicker = (ColorPickerPreference) findPreference(LOCKSCREEN_CLOCK_COLOR);
        mLockscreenClockColorPicker.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(resolver,
                    Settings.System.LOCKSCREEN_CLOCK_COLOR, DEFAULT);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mLockscreenClockColorPicker.setSummary(hexColor);
        mLockscreenClockColorPicker.setNewPreviewColor(intColor);

        mLockscreenClockDateColorPicker = (ColorPickerPreference) findPreference(LOCKSCREEN_CLOCK_DATE_COLOR);
        mLockscreenClockDateColorPicker.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(resolver,
                    Settings.System.LOCKSCREEN_CLOCK_DATE_COLOR, DEFAULT);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mLockscreenClockDateColorPicker.setSummary(hexColor);
        mLockscreenClockDateColorPicker.setNewPreviewColor(intColor);

        mLockscreenColorsReset = (Preference) findPreference(LOCKSCREEN_COLORS_RESET);
        PreferenceCategory mFpCategory = (PreferenceCategory) findPreference(LOCKSCREEN_FP_CATEGORY);

        mFPMgr = (FingerprintManager) getActivity().getSystemService(Context.FINGERPRINT_SERVICE);

        if (!mFPMgr.isHardwareDetected()){
            mLSPrefScreen.removePreference(mFpCategory);
        }

        setHasOptionsMenu(true);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mLockscreenOwnerInfoColorPicker) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.LOCKSCREEN_OWNER_INFO_COLOR, intHex);
            return true;
        } else if (preference == mLockscreenAlarmColorPicker) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.LOCKSCREEN_ALARM_COLOR, intHex);
            return true;
        } else if (preference == mLockscreenClockColorPicker) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.LOCKSCREEN_CLOCK_COLOR, intHex);
            return true;
        } else if (preference == mLockscreenClockDateColorPicker) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.LOCKSCREEN_CLOCK_DATE_COLOR, intHex);
            return true;
         }
         return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_RESET, 0, R.string.reset)
                .setIcon(com.android.internal.R.drawable.ic_menu_refresh)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_RESET:
                resetToDefault();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void resetToDefault() {
         AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
         alertDialog.setTitle(R.string.lockscreen_colors_reset_title);
         alertDialog.setMessage(R.string.lockscreen_colors_reset_message);
         alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface dialog, int id) {
                 resetValues();
             }
         });
         alertDialog.setNegativeButton(R.string.cancel, null);
         alertDialog.create().show();
    }

    private void resetValues() {
         ContentResolver resolver = getActivity().getContentResolver();
         Settings.System.putInt(resolver,
                 Settings.System.LOCKSCREEN_OWNER_INFO_COLOR, DEFAULT);
         mLockscreenOwnerInfoColorPicker.setNewPreviewColor(DEFAULT);
         mLockscreenOwnerInfoColorPicker.setSummary(R.string.default_string);
         Settings.System.putInt(resolver,
                 Settings.System.LOCKSCREEN_ALARM_COLOR, DEFAULT);
         mLockscreenAlarmColorPicker.setNewPreviewColor(DEFAULT);
         mLockscreenAlarmColorPicker.setSummary(R.string.default_string);
         Settings.System.putInt(resolver,
                 Settings.System.LOCKSCREEN_CLOCK_COLOR, DEFAULT);
         mLockscreenClockColorPicker.setNewPreviewColor(DEFAULT);
         mLockscreenClockColorPicker.setSummary(R.string.default_string);
         Settings.System.putInt(resolver,
                 Settings.System.LOCKSCREEN_CLOCK_DATE_COLOR, DEFAULT);
         mLockscreenClockDateColorPicker.setNewPreviewColor(DEFAULT);
         mLockscreenClockDateColorPicker.setSummary(R.string.default_string);
    }

}
