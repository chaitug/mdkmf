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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import in.vasista.vsales.R;
import in.vasista.vsales.db.IndentsDataSource;
import in.vasista.vsales.indent.Indent;
import in.vasista.vsales.indent.IndentListFragment;
import in.vasista.vsales.sync.ServerSync;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

/**
 * This is the activity for feature 2 in the dashboard application.
 * It displays some text and provides a way to get back to the home activity.
 *
 */

public class IndentActivity extends DashboardActivity 
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
    //setContentView (R.layout.activity_f2);
    //setTitleFromActivityLabel (R.id.title_text);
	setContentView(R.layout.indent_layout);

}

protected void onResume ()
{
   super.onResume ();
   IndentsDataSource datasource = new IndentsDataSource(this);
   datasource.open();
   List<Indent> indents = datasource.getAllIndents();
   boolean fetchIndents = false;
   if (indents.size() > 0) {
	   SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
	   for (int i=0; i < indents.size(); ++i) {
		   Indent indent = indents.get(i);
		   boolean isInactiveIndent =  fmt.format(new Date()).compareTo(fmt.format(indent.getSupplyDate())) > 0;
		   if (isInactiveIndent) {
			   fetchIndents = true;
			   break;
		   }
	   }
   }
   else {     
	   fetchIndents = true;
   }
   if (fetchIndents) {
	   FragmentManager fm = getFragmentManager();
	   IndentListFragment indentListFragment = (IndentListFragment) fm.findFragmentById(R.id.indent_list_fragment);
	   ProgressBar progressBar = (ProgressBar) findViewById(R.id.indentsRefreshProgress);
	   progressBar.setVisibility(View.VISIBLE);
	   ServerSync serverSync = new ServerSync(this);
	   serverSync.fetchActiveIndents(progressBar, indentListFragment);	
   }
   datasource.close();
   
}
    
} // end class
