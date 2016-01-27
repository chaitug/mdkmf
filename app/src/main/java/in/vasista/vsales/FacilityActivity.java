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

import in.vasista.vsales.R;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

/**
 * This is the activity for feature 5 in the dashboard application.
 * It displays some text and provides a way to get back to the home activity.
 *
 */

public class FacilityActivity extends DashboardActivity 
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
    //setContentView (R.layout.activity_f5);
    //setTitleFromActivityLabel (R.id.title_text);
	setContentView(R.layout.facility_layout);

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
    
} // end class
