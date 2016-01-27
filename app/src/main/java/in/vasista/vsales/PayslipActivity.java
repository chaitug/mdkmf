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


import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.widget.ProgressBar;

/**
 * This is the activity for feature 1 in the dashboard application.
 * It displays some text and provides a way to get back to the home activity.
 *
 */

public class PayslipActivity extends DashboardAppCompatActivity 
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

protected void onCreate(Bundle savedInstanceState) 
{
    super.onCreate(savedInstanceState);
    //setContentView (R.layout.activity_f1);
    //setTitleFromActivityLabel (R.id.title_text);
	setContentChildView(R.layout.payslip_layout);
	setPageTitle(R.string.title_employeeplayslip);

	ProgressBar progressBar = new ProgressBar(this);
	progressBar.setId(R.id.progress_sync);
	//progressBar.setVisibility(View.GONE);
	progressBar.setIndeterminate(true);
	getSupportActionBar().setDisplayShowCustomEnabled(true);
	ActionBar.LayoutParams params = new ActionBar.LayoutParams(
			ActionBar.LayoutParams.WRAP_CONTENT,
			ActionBar.LayoutParams.WRAP_CONTENT,
			Gravity.RIGHT);
	getSupportActionBar().setCustomView(progressBar, params);
}
    
} // end class
