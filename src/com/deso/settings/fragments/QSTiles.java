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

import android.content.Context;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.support.v7.preference.Preference;

import com.android.settings.R;
import com.deso.settings.preferences.CustomSeekBarPreference;
import com.android.internal.logging.MetricsProto.MetricsEvent;

import java.util.List;
import java.util.ArrayList;

public class QSTiles extends DesoSettingsFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String KEY_SYSUI_QQS_COUNT = "sysui_qqs_count_key";

    private CustomSeekBarPreference mSysuiQqsCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getResources().getString(R.string.qs_tiles_title);
        addPreferencesFromResource(R.xml.qs_tiles_settings);

        mSysuiQqsCount = (CustomSeekBarPreference) findPreference(KEY_SYSUI_QQS_COUNT);
        int SysuiQqsCount = Settings.Secure.getInt(getContentResolver(),
                Settings.Secure.QQS_COUNT, 5);
        mSysuiQqsCount.setValue(SysuiQqsCount / 1);
        mSysuiQqsCount.setOnPreferenceChangeListener(this);

    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mSysuiQqsCount) {
            int SysuiQqsCount = (Integer) newValue;
            Settings.Secure.putInt(getActivity().getContentResolver(),
                    Settings.Secure.QQS_COUNT, SysuiQqsCount * 1);
            return true;
        }
        return false;
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.DESO;
    }
}
