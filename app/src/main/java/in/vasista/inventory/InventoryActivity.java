package in.vasista.inventory;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import in.vasista.vsales.DashboardAppCompatActivity;
import in.vasista.milkosoft.mdkmf.R;

public class InventoryActivity extends DashboardAppCompatActivity
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
MenuItem menuItem;
protected void onCreate(Bundle savedInstanceState)  
{
    super.onCreate(savedInstanceState);
    //setContentView (R.layout.activity_f5);
    //setTitleFromActivityLabel (R.id.title_text); 
	setContentChildView(R.layout.inventory_layout);
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
		getMenuInflater().inflate(R.menu.refresh, menu);
		return super.onCreateOptionsMenu(menu);
	}
	public boolean onOptionsItemSelected(MenuItem item){
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
			case R.id.action_refresh:
				menuItem = item;
				menuItem.setActionView(R.layout.progressbar);
				//ProgressBar progressBar=(ProgressBar)menuItem.getActionView().findViewById(R.id.menuitem_progress);
				android.app.FragmentManager fm = getFragmentManager();
				InventoryListFragment inventoryListFragment = (InventoryListFragment) fm.findFragmentById(R.id.inventory_list_fragment);
				inventoryListFragment.syncInventory(menuItem);
				return true;

		}
		return false;
	}
    
} // end class
