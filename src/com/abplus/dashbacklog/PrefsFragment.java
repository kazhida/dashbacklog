package com.abplus.dashbacklog;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Copyright (C) 2013 ABplus Inc. kazhida
 * All rights reserved.
 * Author:  kazhida
 * Created: 2013/05/08 14:02
 */
public class PrefsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
    }
}
