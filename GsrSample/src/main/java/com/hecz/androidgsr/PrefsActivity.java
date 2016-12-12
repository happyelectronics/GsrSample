package com.hecz.androidgsr;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by Michael on 16.7.2015.
 */
public class PrefsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);


    }





}
