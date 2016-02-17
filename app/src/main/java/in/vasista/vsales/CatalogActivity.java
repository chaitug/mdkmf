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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import in.vasista.vsales.catalog.CatalogListFragment;

/**
 * This is the activity for feature 4 in the dashboard application.
 * It displays some text and provides a way to get back to the home activity.
 *
 */

public class CatalogActivity extends DashboardAppCompatActivity
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
 */

    ProgressBar progressBar;
protected void onCreate(Bundle savedInstanceState) 
{
    super.onCreate(savedInstanceState);
    //setContentView (R.layout.activity_f4);
    //setTitleFromActivityLabel (R.id.title_text);
	setContentChildView(R.layout.catalog_layout);
    setSalesDashboardTitle(R.string.title_feature4);

}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh, menu);
        return super.onCreateOptionsMenu(menu);
    }
    public boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.action_refresh:
                item.setActionView(R.layout.progressbar);
                progressBar=(ProgressBar) item.getActionView().findViewById(R.id.menuitem_progress);
                android.app.FragmentManager fm = getFragmentManager();
                CatalogListFragment catalogListFragment = (CatalogListFragment) fm.findFragmentById(R.id.catalog_list_fragment);
                catalogListFragment.syncCatalog(item);
                return true;

        }
        return false;
    }
} // end class
