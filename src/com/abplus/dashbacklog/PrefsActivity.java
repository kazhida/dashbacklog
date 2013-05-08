package com.abplus.dashbacklog;

import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import java.util.List;

/**
 * Copyright (C) 2013 ABplus Inc. kazhida
 * All rights reserved.
 * Author:  kazhida
 * Created: 2013/05/08 12:09
 */
public class PrefsActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            oldStylePreferencesFromResource();
        }
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        super.onBuildHeaders(target);
        loadHeadersFromResource(R.xml.prefs_fragment, target);
    }

    @SuppressWarnings("deprecation")
    private void oldStylePreferencesFromResource() {
        addPreferencesFromResource(R.xml.prefs);
    }
}
