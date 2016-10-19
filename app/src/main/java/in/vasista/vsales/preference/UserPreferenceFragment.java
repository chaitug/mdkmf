package in.vasista.vsales.preference;

import in.vasista.milkosoft.mdkmf.R;
import android.os.Bundle;
import android.preference.PreferenceFragment;


public class UserPreferenceFragment extends PreferenceFragment{

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		addPreferencesFromResource(R.xml.userpreferences);		
	}
}
