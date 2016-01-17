package com.entekhab.settings;

import com.entekhab.R;
import com.entekhab.SettingsActivity;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.MenuItem;


public class InfoFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref_info);
		
		setHasOptionsMenu(true);
		((SettingsActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case android.R.id.home:
			getActivity().onBackPressed();
			return true;
		}
		return false;
	}
}
