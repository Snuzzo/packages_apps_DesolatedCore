/*
 * Copyright (C) 2017 The Pure Nexus Project
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

package com.deso.settings;

import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceCategory;

import com.android.internal.logging.MetricsProto.MetricsEvent;

import com.android.settings.SettingsPreferenceFragment;

public class DesoSettings extends SettingsPreferenceFragment {

    private PreferenceCategory mLedsCategory;
    private Preference mChargingLeds;
    private Preference mNotificationLeds;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.deso_settings_main);
        PreferenceScreen prefScreen = getPreferenceScreen();

        mLedsCategory = (PreferenceCategory) findPreference("leds");
        mChargingLeds = (Preference) findPreference("charging_light");
        mNotificationLeds = (Preference) findPreference("notification_light");
        if (mChargingLeds != null
                && !getResources().getBoolean(
                        com.android.internal.R.bool.config_intrusiveBatteryLed)) {
            mLedsCategory.removePreference(mChargingLeds);
        }
        if (mNotificationLeds != null
                && !getResources().getBoolean(
                        com.android.internal.R.bool.config_intrusiveNotificationLed)) {
            mLedsCategory.removePreference(mNotificationLeds);
        }
        if (mChargingLeds == null && mNotificationLeds == null) {
            getPreferenceScreen().removePreference(mLedsCategory);
        }
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.DESO;
    }
}

