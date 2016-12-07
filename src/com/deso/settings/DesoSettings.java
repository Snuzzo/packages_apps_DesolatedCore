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


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.widget.Toolbar;
import android.support.v13.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import com.deso.settings.fragments.ClockSettings;
import com.deso.settings.fragments.DesoSettingsFragment;
import com.deso.settings.fragments.LedSettings;
import com.deso.settings.fragments.LockscreenSettings;
import com.deso.settings.fragments.PowerMenuSettings;
import com.deso.settings.fragments.StatusBarSettings;
import com.deso.settings.fragments.RecentsSettings;
import com.deso.settings.fragments.QSSettings;
import com.deso.settings.fragments.VolumeRockerSettings;
import com.deso.settings.fragments.MiscSettings;
//import com.deso.settings.fragments.AboutDeso;

import com.android.internal.logging.MetricsProto.MetricsEvent;
import com.android.settings.SettingsPreferenceFragment;

public class DesoSettings extends SettingsPreferenceFragment {

    ViewPager mViewPager;
    ViewGroup mContainer;
    PagerTabStrip mTabs;
    SectionsPagerAdapter mSectionsPagerAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContainer = container;

        View view = inflater.inflate(R.layout.main_layout, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mTabs = (PagerTabStrip) view.findViewById(R.id.tabs);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(1);
        mViewPager.postDelayed(new Runnable() {
            @Override
            public void run() {
                mViewPager.setCurrentItem(0);
            }
        },100);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle saveState) {
        super.onSaveInstanceState(saveState);
    }

    class SectionsPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<DesoSettingsFragment> frags = new ArrayList<DesoSettingsFragment>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            frags.add((DesoSettingsFragment) new StatusBarSettings());
            if(DesoSettings.this.getResources().getBoolean(com.android.internal.R.bool.config_intrusiveBatteryLed) ||
               DesoSettings.this.getResources().getBoolean(com.android.internal.R.bool.config_intrusiveNotificationLed))
               frags.add((DesoSettingsFragment) new LedSettings());
            frags.add((DesoSettingsFragment) new PowerMenuSettings());
            frags.add((DesoSettingsFragment) new LockscreenSettings());
            frags.add((DesoSettingsFragment) new RecentsSettings());
            frags.add((DesoSettingsFragment) new QSSettings());
            frags.add((DesoSettingsFragment) new VolumeRockerSettings());
            frags.add((DesoSettingsFragment) new MiscSettings());
            //frags.add((DesoSettingsFragment) new AboutDeso());
        }

        @Override
        public Fragment getItem(int position) {
            return (Fragment) frags.get(position);
        }

        @Override
        public int getCount() {
            return frags.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return ((DesoSettingsFragment) frags.get(position)).getTitle();
        }
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.DESO;
    }
}

