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
import com.android.settings.preference.SecureSettingSwitchPreference;
import com.android.internal.logging.MetricsProto.MetricsEvent;
import com.deso.settings.preferences.CustomSeekBarPreference;

import com.android.internal.util.deso.Helpers;

public class LockscreenSettings extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String LOCKSCREEN_FP_CATEGORY = "fp_ls_category";
    private static final String LOCKSCREEN_PREF = "ls_preferences";
    private static final String LOCKSCREEN_MAX_NOTIF_CONFIG = "lockscreen_max_notif_config";
    private static final String LOCK_QS_DISABLED = "lockscreen_qs_disabled";
    private static final String PREF_LOCKSCREEN_SHORTCUTS_LONGPRESS = "lockscreen_shortcuts_longpress";

    private Context mContext;
    private CustomSeekBarPreference mMaxKeyguardNotifConfig;
    private Preference mLockscreenColorsReset;
    private FingerprintManager mFPMgr;
    private SecureSettingSwitchPreference mLockQSDisabled;
    private SwitchPreference mLockscreenShortcutsLongpress;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.lockscreen_settings);

        ContentResolver resolver = getActivity().getContentResolver();
        PreferenceScreen mLSPrefScreen = (PreferenceScreen) findPreference(LOCKSCREEN_PREF);
        // LockscreenColors
        mContext = getActivity().getApplicationContext();

        PreferenceCategory mFpCategory = (PreferenceCategory) findPreference(LOCKSCREEN_FP_CATEGORY);

        mFPMgr = (FingerprintManager) getActivity().getSystemService(Context.FINGERPRINT_SERVICE);

        mMaxKeyguardNotifConfig = (CustomSeekBarPreference) findPreference(LOCKSCREEN_MAX_NOTIF_CONFIG);
        int kgconf = Settings.System.getInt(getContentResolver(),
                Settings.System.LOCKSCREEN_MAX_NOTIF_CONFIG, 5);
        mMaxKeyguardNotifConfig.setValue(kgconf);
        mMaxKeyguardNotifConfig.setOnPreferenceChangeListener(this);

        mLockscreenShortcutsLongpress = (SwitchPreference) findPreference(
                PREF_LOCKSCREEN_SHORTCUTS_LONGPRESS);
        mLockscreenShortcutsLongpress.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.LOCKSCREEN_SHORTCUTS_LONGPRESS, 1) == 1);
        mLockscreenShortcutsLongpress.setOnPreferenceChangeListener(this);

        if (!mFPMgr.isHardwareDetected()){
            mLSPrefScreen.removePreference(mFpCategory);
        }
        mLockQSDisabled = (SecureSettingSwitchPreference) findPreference(LOCK_QS_DISABLED);
        mLockQSDisabled.setOnPreferenceChangeListener(this);

        setHasOptionsMenu(true);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mMaxKeyguardNotifConfig) {
            int kgconf = (Integer) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.LOCKSCREEN_MAX_NOTIF_CONFIG, kgconf);
            return true;
        } else if (preference == mLockQSDisabled) {
            Helpers.restartSystemUI(mContext);
            return true;
        } else if (preference == mLockscreenShortcutsLongpress) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_SHORTCUTS_LONGPRESS,
                    (Boolean) newValue ? 1 : 0);
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
         super.onResume();
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.DESO;
    }

}
