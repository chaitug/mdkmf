/*
 * Copyright (C) 2011 Wglxy.com
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

package in.vasista.vsales;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import in.vasista.milkosoft.mdkmf.R;

import in.vasista.vsales.facility.FacilityListFragment;

/**
 * This is the activity for feature 5 in the dashboard application.
 * It displays some text and provides a way to get back to the home activity.
 *
 */

public class FacilityActivity extends DashboardAppCompatActivity
{

/**
 * onCreate
 *
 * Called when the activity is first created. 
 * This is where you should do all of your normal static set up: create views, bind data to lists, etc. 
 * This method also provides you with a Bundle containing the activity's previously frozen state, if there was one.
 * 
 * Always followed by onStart().
 *
 * @param savedInstanceState Bundle
 */
private MenuItem menuItem;
	ProgressBar progressBar;
protected void onCreate(Bundle savedInstanceState) 
{
    super.onCreate(savedInstanceState);
    //setContentView (R.layout.activity_f5);
    //setTitleFromActivityLabel (R.id.title_text);
	setContentChildView(R.layout.facility_layout);
	getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
			| ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);
	actionBarHomeEnabled();
}

/**
 * onResume
 * Called when the activity will start interacting with the user. 
 * At this point your activity is at the top of the activity stack, with user input going to it.
 * Always followed by onPause().
 *
 */

protected void onResume ()
{
   super.onResume ();
	final FrameLayout inputSearchFrame = (FrameLayout) findViewById(R.id.inputSearchFrame);   
	//final EditText inputSearch = (EditText) findViewById(R.id.inputSearch);
	if (inputSearchFrame != null) {
		inputSearchFrame.setVisibility(View.GONE);	
		//inputSearchFrame.setText("");
	}
   
}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		menu.removeItem(R.id.action_about);
		menu.removeItem(R.id.action_settings);
		return super.onCreateOptionsMenu(menu);
	}
	public boolean onOptionsItemSelected(MenuItem item){
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
			case R.id.action_refresh:
				menuItem = item;
				menuItem.setActionView(R.layout.progressbar);
				progressBar=(ProgressBar)menuItem.getActionView().findViewById(R.id.menuitem_progress);
				FragmentManager fm = getFragmentManager();
				FacilityListFragment facilityListFragment = (FacilityListFragment) fm.findFragmentById(R.id.facility_list_fragment);
				facilityListFragment.syncOutlets(menuItem);
				return true;
			case  R.id.homeSearch:
				final FrameLayout inputSearchFrame = (FrameLayout) findViewById(R.id.inputSearchFrame);
				if (inputSearchFrame.isShown()) {
					inputSearchFrame.setVisibility(View.GONE);
				}
				else {
					inputSearchFrame.setVisibility(View.VISIBLE);
				}
				return true;

		}
		return false;
	}
} // end class
