package in.vasista.vsales.preference;


import in.vasista.vsales.R;

import java.util.List;

import android.preference.PreferenceActivity;

public class FragmentPreferences extends PreferenceActivity {

  @Override 
  public void onBuildHeaders(List<Header> target) {
    loadHeadersFromResource(R.xml.preference_headers, target);  
  }
}