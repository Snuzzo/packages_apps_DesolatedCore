/*
 * Copyright (C) 2017 DesolationROM
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
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceCategory;

import com.android.internal.logging.MetricsProto.MetricsEvent;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class LedSettings extends DesoSettingsFragment {

    private Preference mLeds;
    private Preference mChargingLeds;
    private Preference mNotificationLeds;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getResources().getString(R.string.leds);
        addPreferencesFromResource(R.xml.led_settings);
        final PreferenceScreen prefScreen = getPreferenceScreen();

        mChargingLeds = (Preference) findPreference("charging_light");
        mNotificationLeds = (Preference) findPreference("notification_light");
        mLeds = (Preference) findPreference("deso_leds");

        if (mChargingLeds == null && mNotificationLeds == null) {
            prefScreen.removePreference(mLeds);
        }
        if (mChargingLeds != null
                && !getResources().getBoolean(
                        com.android.internal.R.bool.config_intrusiveBatteryLed)) {
            prefScreen.removePreference(mChargingLeds);
        }
        if (mNotificationLeds != null
                && !getResources().getBoolean(
                        com.android.internal.R.bool.config_intrusiveNotificationLed)) {
            prefScreen.removePreference(mNotificationLeds);
        }
    }

}

