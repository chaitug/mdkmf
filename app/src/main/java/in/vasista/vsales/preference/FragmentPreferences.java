package in.vasista.vsales.preference;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;

import in.vasista.global.GlobalApplication;
import in.vasista.vsales.R;

public class FragmentPreferences extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

  @Override 
  public void onBuildHeaders(List<Header> target) {
    loadHeadersFromResource(R.xml.preference_headers, target);  
  }
  @Override
  protected boolean isValidFragment(String fragmentName) {
    return true;
  }
  Toolbar mToolbar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
    LinearLayout content = (LinearLayout) root.getChildAt(0);
    LinearLayout toolbarContainer = (LinearLayout) View.inflate(this, R.layout.toolbar, null);

    root.removeAllViews();
    toolbarContainer.addView(content);
    root.addView(toolbarContainer);

      mToolbar = (Toolbar) toolbarContainer.findViewById(R.id.toolbar);
      mToolbar.setTitle(R.string.action_settings);
      mToolbar.setTitleTextColor(getResources().getColor(R.color.icons));
      mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          finish();
        }
      });

    PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    //Toast.makeText(getApplicationContext(), "Pref changed " + key, Toast.LENGTH_LONG).show();

    if (key.equalsIgnoreCase("password")||key.equalsIgnoreCase("serverURL")||key.equalsIgnoreCase("userName")||key.equalsIgnoreCase("tenantId")){
      ((GlobalApplication)getApplication()).setPrefChange(true);
    }

  }
}