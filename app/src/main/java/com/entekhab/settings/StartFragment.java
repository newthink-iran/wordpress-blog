package com.entekhab.settings ;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.entekhab.R;


public class StartFragment extends PreferenceFragment {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.pref_start);
    }
}
